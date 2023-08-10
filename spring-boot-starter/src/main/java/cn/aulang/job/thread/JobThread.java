package cn.aulang.job.thread;

import cn.aulang.job.core.context.JobContext;
import cn.aulang.job.core.context.JobHelper;
import cn.aulang.job.core.handler.IJobHandler;
import cn.aulang.job.core.log.JobFileAppender;
import cn.aulang.job.core.model.CallbackParam;
import cn.aulang.job.core.model.TriggerParam;
import cn.aulang.job.executor.CallbackExecutor;
import cn.aulang.job.service.JobExecutorService;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 任务执行线程
 *
 * @author wulang
 */
@Slf4j
public class JobThread implements Runnable {

    private final IJobHandler jobHandler;
    private final TriggerParam triggerParam;
    private final CallbackExecutor callbackExecutor;

    public JobThread(IJobHandler jobHandler, TriggerParam triggerParam, CallbackExecutor callbackExecutor) {
        this.jobHandler = jobHandler;
        this.triggerParam = triggerParam;
        this.callbackExecutor = callbackExecutor;
    }

    @Override
    public void run() {
        Date logDateTime = new Date(triggerParam.getLogDateTime());
        Date lastDateTime = new Date(triggerParam.getLastDateTime());

        String jobLogFileName = JobFileAppender.makeLogFileName(logDateTime, triggerParam.getLogId());

        JobContext context = new JobContext(
                triggerParam.getJobId(),
                triggerParam.getLogId(),
                logDateTime,
                lastDateTime,
                triggerParam.getHandlerParam(),
                jobLogFileName,
                triggerParam.getShardIndex(),
                triggerParam.getShardTotal()
        );
        JobContext.setJobContext(context);

        JobHelper.log("Job start running, job id: {}, log id: {}", context.getJobId(), context.getLogId());

        try {
            jobHandler.before();
        } catch (Exception e) {
            log.error("JobHandler: " + jobHandler.name() + " before method execution fail", e);

            // 输出错误日志
            String handleMsg = "Job before method execution fail";
            JobHelper.log(handleMsg);
            JobHelper.log(e);

            // 失败退出
            JobHelper.handleFail(handleMsg);
            finishCallback(context);
            return;
        }

        try {
            if (triggerParam.getTimeout() > 0) {
                // 超时
                FutureTask<Boolean> futureTask = new FutureTask<>(() -> {
                    JobContext.setJobContext(context);
                    jobHandler.execute();
                    return true;
                });

                Thread futureThread = new Thread(futureTask);
                futureThread.start();

                try {
                    futureTask.get(triggerParam.getTimeout(), TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    String handleMsg = "Job execution timeout, execution terminated";

                    JobHelper.log(handleMsg);
                    JobHelper.log(e);

                    // 超时退出
                    JobHelper.handleTimeout(handleMsg);
                    finishCallback(context);
                    return;
                } finally {
                    // 中断线程
                    futureThread.interrupt();
                }
            } else {
                // 不需要超时
                jobHandler.execute();
            }

            if (context.getHandleCode() <= 0) {
                JobHelper.handleFail("Job handle result lost");
            } else {
                // 结果太长截断
                String handleMsg = context.getHandleMsg();
                handleMsg = (handleMsg != null && handleMsg.length() > 65535) ? handleMsg.substring(0, 65535).concat("...") : handleMsg;
                context.setHandleMsg(handleMsg);
            }

            JobHelper.log("Job execution end, code: {}, msg: {}", context.getHandleCode(), context.getHandleMsg());
        } catch (Exception e) {
            log.error("JobHandler: " + jobHandler.name() + " execution fail", e);
            JobHelper.handleFail(e.getMessage());

            JobHelper.log(e);
        }

        try {
            jobHandler.after();
        } catch (Exception e) {
            log.error("JobHandler: " + jobHandler.name() + "after method execute fail", e);

            // 输出错误日志
            String handleMsg = "Job after method execution fail";
            JobHelper.log(handleMsg);
            JobHelper.log(e);

            // 失败任务退出
            JobHelper.handleFail(handleMsg);
        }

        finishCallback(context);
    }

    private void finishCallback(JobContext context) {
        // 执行结束
        JobExecutorService.finishJob(context.getJobId(), context.getLogId());
        // 回调结果
        callbackExecutor.execute(new CallbackParam(context.getJobId(), context.getLogId(),
                context.getHandleCode(), context.getHandleMsg()));
    }
}