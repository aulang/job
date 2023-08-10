package cn.aulang.job.admin.service;

import cn.aulang.common.crud.CRUDService;
import cn.aulang.job.admin.cron.CronExpression;
import cn.aulang.job.admin.dao.JobExecutorDao;
import cn.aulang.job.admin.dao.JobInfoDao;
import cn.aulang.job.admin.dao.JobLogDao;
import cn.aulang.job.admin.enums.BlockStrategyEnum;
import cn.aulang.job.admin.enums.JobStatusEnum;
import cn.aulang.job.admin.enums.MisfireStrategyEnum;
import cn.aulang.job.admin.enums.RouteStrategyEnum;
import cn.aulang.job.admin.enums.ScheduleTypeEnum;
import cn.aulang.job.admin.exception.JobException;
import cn.aulang.job.admin.model.po.JobChild;
import cn.aulang.job.admin.model.po.JobExecutor;
import cn.aulang.job.admin.model.po.JobGlueCode;
import cn.aulang.job.admin.model.po.JobInfo;
import cn.aulang.job.admin.model.vo.JobVO;
import cn.aulang.job.admin.scheduler.JobScheduleHelper;
import cn.aulang.job.admin.utils.NumberUtils;
import cn.aulang.job.core.enums.GlueTypeEnum;
import cn.aulang.job.core.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.page.Pageable;
import tk.mybatis.mapper.page.SimplePage;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务服务
 *
 * @author wulang
 */
@Slf4j
@Service
public class JobInfoService extends CRUDService<JobInfo, Long> {

    private final JobLogDao logDao;
    private final JobInfoDao jobDao;
    private final JobExecutorDao executorDao;
    private final JobGlueCodeService glueCodeService;

    @Autowired
    public JobInfoService(JobLogDao logDao,
                          JobInfoDao jobDao,
                          JobExecutorDao executorDao,
                          JobGlueCodeService glueCodeService) {
        this.logDao = logDao;
        this.jobDao = jobDao;
        this.executorDao = executorDao;
        this.glueCodeService = glueCodeService;
    }

    @Override
    protected JobInfoDao getRepository() {
        return jobDao;
    }

    public void update(JobInfo jobInfo) {
        jobDao.update(jobInfo);
    }

    public Pageable<JobVO> page(Long executorId,
                                String name,
                                String groupName,
                                Integer status,
                                String executorHandler,
                                String author,
                                int page,
                                int size) {
        Pageable<JobVO> pageable = new SimplePage<>(page, size);
        return pageable.setList(jobDao.findBy(executorId, name, groupName, status, executorHandler, author, pageable));
    }

    public List<String> findGroupNames() {
        return jobDao.findGroupNames();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(JobInfo jobInfo) {
        JobExecutor executor = executorDao.get(jobInfo.getExecutorId());
        if (executor == null) {
            throw new JobException("Executor id: " + jobInfo.getExecutorId() + " not exists");
        }

        String scheduleConf = jobInfo.getScheduleConf();
        ScheduleTypeEnum scheduleType = ScheduleTypeEnum.match(jobInfo.getScheduleType(), null);

        if (scheduleType == ScheduleTypeEnum.CRON) {
            if (StringUtils.isBlank(scheduleConf)
                    || !CronExpression.isValidExpression(scheduleConf)) {
                throw new JobException("Cron: " + scheduleConf + " invalid");
            }
        } else if (scheduleType == ScheduleTypeEnum.FIX_RATE) {
            if (!NumberUtils.isInteger(scheduleConf)) {
                throw new JobException("Fix rate: " + scheduleConf + " invalid");
            }

            int fixSecond = Integer.parseInt(jobInfo.getScheduleConf());
            if (fixSecond < 1) {
                throw new JobException("Fix rate: " + scheduleConf + " invalid");
            }
        } else {
            throw new JobException("ScheduleType: " + jobInfo.getScheduleType() + " invalid");
        }

        GlueTypeEnum glueTypeEnum = GlueTypeEnum.match(jobInfo.getGlueType());
        if (glueTypeEnum == null) {
            throw new JobException("glueType: " + jobInfo.getGlueType() + " invalid");
        }

        if (glueTypeEnum == GlueTypeEnum.BEAN && StringUtils.isBlank(jobInfo.getExecutorHandler())) {
            throw new JobException("BEAN model executorHandler can not be blank");
        }

        if (MisfireStrategyEnum.match(jobInfo.getMisfireStrategy(), null) == null) {
            throw new JobException("misfireStrategy: " + jobInfo.getMisfireStrategy() + " invalid");
        }

        if (RouteStrategyEnum.match(jobInfo.getRouteStrategy(), null) == null) {
            throw new JobException("routeStrategy: " + jobInfo.getRouteStrategy() + " invalid");
        }

        if (BlockStrategyEnum.match(jobInfo.getBlockStrategy(), null) == null) {
            throw new JobException("blockStrategy: " + jobInfo.getBlockStrategy() + " invalid");
        }

        Date now = new Date();

        if (!jobInfo.isNew()) {
            JobInfo dbJobInfo = jobDao.get(jobInfo.getId());
            if (dbJobInfo == null) {
                throw new JobException("Job id: " + jobInfo.getId() + " not exists");
            }

            boolean notChanged = StringUtils.equals(jobInfo.getScheduleType(), dbJobInfo.getScheduleType())
                    && StringUtils.equals(jobInfo.getScheduleConf(), dbJobInfo.getScheduleConf());
            if (!notChanged && dbJobInfo.getStatus() == JobStatusEnum.RUNNING.getCode()) {
                Long nextTriggerTime = JobScheduleHelper.nextTime(jobInfo,
                        new Date(System.currentTimeMillis() + JobScheduleHelper.PRE_READ_MILLISECONDS));

                if (nextTriggerTime == null) {
                    throw new JobException("Running job scheduleType: " + jobInfo.getScheduleType() + " invalid");
                }
                jobInfo.setTriggerNextTime(nextTriggerTime);
            } else {
                jobInfo.setTriggerNextTime(dbJobInfo.getTriggerNextTime());
            }

            jobInfo.setTriggerLastTime(dbJobInfo.getTriggerLastTime());
            jobInfo.setCreateTime(dbJobInfo.getCreateTime());
            jobInfo.setStatus(dbJobInfo.getStatus());
            jobInfo.setUpdateTime(now);
        } else {
            jobInfo.setCreateTime(now);
            jobInfo.setTriggerNextTime(0L);
            jobInfo.setTriggerLastTime(0L);
            jobInfo.setStatus(JobStatusEnum.STOP.getCode());
        }

        jobDao.saveOrUpdate(jobInfo);

        saveChildJob(jobInfo, jobInfo.getChildJobIds());
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(JobInfo jobInfo, String glueSource) {
        save(jobInfo);

        JobGlueCode glueCode = new JobGlueCode();

        glueCode.setJobId(jobInfo.getId());
        glueCode.setGlueType(jobInfo.getGlueType());
        glueCode.setGlueSource(glueSource);

        glueCodeService.save(glueCode);
    }

    private void saveChildJob(JobInfo jobInfo, List<Long> childJobIds) {
        Long jobId = jobInfo.getId();

        jobDao.deleteChildJob(jobId);

        if (CollectionUtils.isEmpty(childJobIds)) {
            return;
        }

        List<JobChild> children = childJobIds
                .parallelStream()
                .map(e -> new JobChild(jobId, e))
                .collect(Collectors.toList());

        jobDao.saveChildJob(children);
    }

    @Override
    protected void postRemove(Long id) {
        // 删除JobLog
        logDao.deleteByJobId(id);
        // 删除glueCode
        glueCodeService.deleteByJobId(id);
    }

    public Response<String> stop(Long id) {
        JobInfo jobInfo = jobDao.get(id);

        jobInfo.setStatus(JobStatusEnum.STOP.getCode());
        jobInfo.setTriggerLastTime(0L);
        jobInfo.setTriggerNextTime(0L);

        jobInfo.setUpdateTime(new Date());
        jobDao.update(jobInfo);

        return Response.success();
    }

    public int stop(List<Long> ids) {
        int count = 0;
        for (Long id : ids) {
            try {
                stop(id);
                ++count;
            } catch (Exception e) {
                log.error("Failed to stop job, job id: " + id, e);
            }
        }
        return count;
    }

    public Response<String> start(Long id) {
        JobInfo jobInfo = jobDao.get(id);

        ScheduleTypeEnum scheduleType = ScheduleTypeEnum.match(jobInfo.getScheduleType(), null);
        if (scheduleType == null) {
            return Response.fail("ScheduleType: " + jobInfo.getScheduleType() + " invalid");
        }

        Long nextTriggerTime = JobScheduleHelper.nextTime(jobInfo,
                new Date(System.currentTimeMillis() + JobScheduleHelper.PRE_READ_MILLISECONDS));

        if (nextTriggerTime == null) {
            return Response.fail("Failed to get the scheduling time");
        }

        jobInfo.setStatus(JobStatusEnum.RUNNING.getCode());
        jobInfo.setTriggerLastTime(jobInfo.getTriggerNextTime());
        jobInfo.setTriggerNextTime(nextTriggerTime);

        jobInfo.setUpdateTime(new Date());

        jobDao.update(jobInfo);

        return Response.success();
    }

    public int start(List<Long> ids) {
        int count = 0;
        for (Long id : ids) {
            try {
                Response<String> result = start(id);
                if (result.isSuccess()) {
                    ++count;
                }
            } catch (Exception e) {
                log.error("Failed to start job, job id: " + id, e);
            }
        }
        return count;
    }

    public List<JobInfo> findScheduleJob(long triggerNextTime, Pageable<?> pageable) {
        return jobDao.findByStatusAndTriggerNextTimeLt(1, triggerNextTime, pageable);
    }

    public int updateTriggerNextTime(Long id, long triggerCurrentTime, long triggerNextTime) {
        return jobDao.updateTriggerNextTime(id, triggerCurrentTime, triggerNextTime);
    }

    public List<JobInfo> findChildJobs(Long id) {
        return jobDao.findChildJobs(id);
    }
}
