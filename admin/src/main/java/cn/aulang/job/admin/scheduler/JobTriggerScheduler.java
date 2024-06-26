package cn.aulang.job.admin.scheduler;

import cn.aulang.common.core.concurrent.ThreadFactoryBuilder;
import cn.aulang.job.admin.enums.JobStatusEnum;
import cn.aulang.job.admin.model.po.JobInfo;
import cn.aulang.job.admin.service.JobInfoService;
import cn.aulang.job.admin.service.TriggerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.page.Pageable;
import tk.mybatis.mapper.page.SimplePage;

import java.util.Date;
import java.util.List;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 任务触发调度器
 *
 * @author wulang
 */
@Slf4j
@Component
public class JobTriggerScheduler implements DisposableBean {

    private volatile boolean exit = false;
    private final JobInfoService jobService;
    private final TriggerService triggerService;

    private final ExecutorService delayQueueExecutor;
    private final DelayQueue<DelayTriggerJob> triggerQueue = new DelayQueue<>();
    private final ScheduledExecutorService schedulerExecutor = Executors.newSingleThreadScheduledExecutor(
            Thread.ofVirtual().name("JobTriggerScheduler-").factory());

    @Autowired
    public JobTriggerScheduler(JobInfoService jobService, TriggerService triggerService) {
        this.jobService = jobService;
        this.triggerService = triggerService;

        int threadCount = Math.max(Runtime.getRuntime().availableProcessors(), 4);
        delayQueueExecutor = Executors.newFixedThreadPool(threadCount,
                new ThreadFactoryBuilder().setNameFormat("JobDelayQueueExecutor-%d").build());
        for (int i = 0; i < threadCount; i++) {
            delayQueueExecutor.execute(this::delay);
        }

        // 10秒扫表一次
        schedulerExecutor.scheduleAtFixedRate(this::schedule, 0, JobScheduleHelper.PRE_READ_MILLISECONDS, TimeUnit.MILLISECONDS);
    }

    private void schedule() {
        try {
            roundTriggerJob();
        } catch (Exception e) {
            log.error("Round trigger job fail", e);
        }
    }

    private void roundTriggerJob() {
        long now = System.currentTimeMillis();
        // 往后多取10秒的数据，丢入延时队列
        long currentTriggerTime = now + JobScheduleHelper.PRE_READ_MILLISECONDS;

        int page = 1;
        Pageable<JobInfo> pageable = new SimplePage<>(page, 100);
        List<JobInfo> scheduleJobs = jobService.findScheduleJob(currentTriggerTime, pageable);

        while (!CollectionUtils.isEmpty(scheduleJobs)) {
            for (JobInfo jobInfo : scheduleJobs) {
                Long jobTriggerNextTime = JobScheduleHelper.nextTime(jobInfo, new Date(currentTriggerTime));

                if (jobTriggerNextTime == null) {
                    log.error("Job next trigger time is null, will stop job, jobId: {}, scheduleType: {}, scheduleConf: {}",
                            jobInfo.getId(), jobInfo.getScheduleType(), jobInfo.getScheduleConf());

                    jobTriggerNextTime = 0L;

                    // 停止任务，防止无限循环
                    jobInfo.setStatus(JobStatusEnum.STOP.getCode());
                    jobService.update(jobInfo);
                }

                int ret = jobService.updateTriggerNextTime(jobInfo.getId(), jobInfo.getTriggerNextTime(), jobTriggerNextTime);

                if (ret > 0) {
                    // 缓存上次触发时间
                    jobInfo.setLastTriggerTime(jobInfo.getTriggerLastTime());
                    // 当前次触发时间
                    jobInfo.setTriggerLastTime(jobInfo.getTriggerNextTime());
                    // 下次触发时间
                    jobInfo.setTriggerNextTime(jobTriggerNextTime);

                    log.info("Get job trigger lock success, push job to DelayQueue, job info: {}", jobInfo);

                    if (!triggerQueue.offer(new DelayTriggerJob(jobInfo))) {
                        log.error("Trigger queue is full, current size: {}", triggerQueue.size());
                    }
                }
            }

            pageable = new SimplePage<>(++page, 100);
            scheduleJobs = jobService.findScheduleJob(currentTriggerTime, pageable);
        }
    }

    private void delay() {
        while (true) {
            DelayTriggerJob delayJob;

            try {
                delayJob = triggerQueue.take();
            } catch (InterruptedException e) {
                if (exit) {
                    // 程序退出
                    log.info("Received process exit signal");
                    break;
                } else {
                    // 线程异常中断，不响应
                    log.error("Take trigger queue item fail", e);
                    continue;
                }
            }

            try {
                // 输出日志
                log.info("Get job from DelayQueue, Prepare to trigger the job, job info: {}", delayJob.jobInfo());
                // 真正触发任务执行
                triggerService.trigger(delayJob.jobInfo());
            } catch (Exception e) {
                log.error(delayJob.jobInfo().toString() + " trigger fail", e);
            }
        }
    }

    @Override
    public void destroy() {
        exit = true;

        schedulerExecutor.shutdownNow();
        delayQueueExecutor.shutdownNow();

        if (!triggerQueue.isEmpty()) {
            log.warn("JobTriggerScheduler shutdown, trigger queue size: {}", triggerQueue.size());
        }
    }
}
