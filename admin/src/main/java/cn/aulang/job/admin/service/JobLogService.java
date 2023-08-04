package cn.aulang.job.admin.service;

import cn.aulang.job.admin.client.ExecutorClient;
import cn.aulang.job.admin.config.JobProperties;
import cn.aulang.job.admin.dao.JobLogDao;
import cn.aulang.job.admin.enums.TriggerTypeEnum;
import cn.aulang.job.admin.model.po.JobInfo;
import cn.aulang.job.admin.model.po.JobLog;
import cn.aulang.job.admin.model.vo.JobLogVO;
import cn.aulang.job.admin.utils.IdUtils;
import cn.aulang.job.core.common.Constants;
import cn.aulang.job.core.enums.HandleCodeEnum;
import cn.aulang.job.core.enums.TriggerCodeEnum;
import cn.aulang.job.core.model.IdleBeatParam;
import cn.aulang.job.core.model.KillParam;
import cn.aulang.job.core.model.LogParam;
import cn.aulang.job.core.model.LogResult;
import cn.aulang.job.core.model.Response;
import cn.aulang.common.crud.CRUDService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.page.Pageable;
import tk.mybatis.mapper.page.SimplePage;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务日志服务
 *
 * @author wulang
 */
@Service
public class JobLogService extends CRUDService<JobLog, Long> {

    private final JobLogDao logDao;
    private final JobProperties properties;

    @Autowired
    public JobLogService(JobLogDao logDao, JobProperties properties) {
        this.logDao = logDao;
        this.properties = properties;
    }

    @Override
    protected JobLogDao getRepository() {
        return logDao;
    }

    public void update(JobLog entity) {
        logDao.update(entity);
    }

    public Pageable<JobLogVO> page(Long executorId, Long jobId, String name, Date from, Date to, Integer status,
                                   String sort, int page, int size) {
        Pageable<JobLogVO> pageable = new SimplePage<>(page, size);
        return pageable.setList(logDao.findBy(executorId, jobId, name, from, to, status, sort, pageable));
    }

    public void clearBefore(Long executorId, Long jobId, Date datetime) {
        logDao.deleteByTriggerTimeLt(executorId, jobId, datetime);
    }

    public void clearBeforeNum(Long executorId, Long jobId, int number) {
        Long maxId = null;

        if (number > 0) {
            maxId = logDao.findClearMaxId(executorId, jobId, number);
        }

        logDao.deleteByIdLt(executorId, jobId, maxId);
    }

    public Response<LogResult> logCat(Long logId, Long triggerTime, int fromLineNum, int readLineNum, String address) {
        if (fromLineNum < 0) {
            fromLineNum = 0;
        }

        if (readLineNum < 0) {
            readLineNum = 500;
        }

        JobLog jobLog = logDao.get(logId);

        long logDateTime;

        if (jobLog == null) {
            address = StringUtils.trim(address);
            logDateTime = (triggerTime != null ? triggerTime : System.currentTimeMillis());
        } else {
            address = jobLog.getExecutorAddress();
            logDateTime = jobLog.getTriggerTime().getTime();
        }

        if (StringUtils.isBlank(address)) {
            return Response.fail("Executor address is empty");
        }

        ExecutorClient client = new ExecutorClient(address);
        return client.log(new LogParam(logId, logDateTime, fromLineNum, readLineNum), properties.getAccessToken());
    }

    public Response<String> kill(Long id, String reason) {
        JobLog jobLog = logDao.get(id);

        if (jobLog.getTriggerCode() != TriggerCodeEnum.SUCCESS.getCode()) {
            return Response.fail("Job has completed");
        }

        if (jobLog.getHandleCode() != HandleCodeEnum.RUNNING.getCode()) {
            return Response.fail("Job has completed");
        }

        ExecutorClient client = new ExecutorClient(jobLog.getExecutorAddress());
        Response<String> result = client.kill(new KillParam(jobLog.getJobId(), reason), properties.getAccessToken());

        if (result.isSuccess()) {
            jobLog.setHandleTime(new Date());
            jobLog.setHandleCode(HandleCodeEnum.CANCEL.getCode());
            String handleMsg = jobLog.getHandleMsg() != null ? (jobLog.getHandleMsg() + Constants.CRLF) : StringUtils.EMPTY;
            jobLog.setHandleMsg(handleMsg + "User kill job, reason: " + reason);
            logDao.update(jobLog);
        } else if (result.isNetError()) {
            jobLog.setHandleTime(new Date());
            jobLog.setHandleCode(HandleCodeEnum.FAIL.getCode());
            logDao.update(jobLog);
        }

        return result;
    }

    public void killRunningJobByAddress(String address, String reason) {
        List<JobLog> runningJobs = logDao.findRunningJobByExecutorAddress(address, HandleCodeEnum.RUNNING.getCode());
        for (JobLog jobLog : runningJobs) {
            jobLog.setHandleTime(new Date());
            jobLog.setHandleCode(HandleCodeEnum.CANCEL.getCode());
            String handleMsg = jobLog.getHandleMsg() != null ? (jobLog.getHandleMsg() + Constants.CRLF) : StringUtils.EMPTY;
            jobLog.setHandleMsg(handleMsg + "Job be killed, reason: " + reason);
            logDao.update(jobLog);
        }
    }

    public List<JobLog> findRunningJobWithExecutorAddress(Long jobId) {
        List<JobLog> runningJobs = logDao.findRunningJob(jobId, HandleCodeEnum.RUNNING.getCode());
        return runningJobs.parallelStream().filter(e -> StringUtils.isNotBlank(e.getExecutorAddress())).collect(Collectors.toList());
    }

    public int saveTriggerInfo(JobLog jobLog) {
        return logDao.saveTriggerInfo(jobLog);
    }

    public int saveHandleInfo(JobLog jobLog) {
        return logDao.saveHandleInfo(jobLog);
    }

    public JobLog saveFailLog(JobInfo jobInfo, TriggerTypeEnum triggerType, String shardingParam, String reason) {
        JobLog jobLog = newJobLog(jobInfo, triggerType, shardingParam, 0);

        jobLog.setTriggerCode(TriggerCodeEnum.FAIL.getCode());
        jobLog.setTriggerMsg(reason);

        logDao.insert(jobLog);
        return jobLog;
    }

    public JobLog newJobLog(JobInfo jobInfo, TriggerTypeEnum triggerType, String shardingParam, int failRetry) {
        JobLog jobLog = new JobLog();
        jobLog.setId(IdUtils.longId());
        jobLog.setJobId(jobInfo.getId());
        jobLog.setExecutorId(jobInfo.getExecutorId());
        jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
        jobLog.setExecutorParam(jobInfo.getExecutorParam());
        jobLog.setShardingParam(shardingParam);
        jobLog.setFailRetry(failRetry);
        jobLog.setTriggerType(triggerType.name());
        jobLog.setTriggerTime(new Date());
        return jobLog;
    }

    public void heathCheck() {
        int page = 1;
        ExecutorClient executorClient = new ExecutorClient();

        // 每次100条循环处理
        Pageable<JobLog> pageable = new SimplePage<>(page, 100);
        List<JobLog> runningJobs = logDao.findRunningJob(null, HandleCodeEnum.RUNNING.getCode(), pageable);
        while (!CollectionUtils.isEmpty(runningJobs)) {
            for (JobLog jobLog : runningJobs) {
                int handleCode = HandleCodeEnum.RUNNING.getCode();

                if (StringUtils.isBlank(jobLog.getExecutorAddress())) {
                    // 没有执行器地址，结果丢失
                    handleCode = HandleCodeEnum.LOST.getCode();
                } else {
                    executorClient.setAddress(jobLog.getExecutorAddress());

                    Response<String> result = executorClient.idleBeat(new IdleBeatParam(jobLog.getJobId()), properties.getAccessToken());
                    if (result.isSuccess()) {
                        // 任务已结束，结果丢失
                        handleCode = HandleCodeEnum.LOST.getCode();
                    }

                    if (result.isNetError()) {
                        // 节点不在线，任务失败
                        handleCode = HandleCodeEnum.FAIL.getCode();
                    }
                }

                if (handleCode != HandleCodeEnum.RUNNING.getCode()) {
                    logDao.updateRunningHandleCode(jobLog.getId(), jobLog.getHandleCode(), handleCode);
                }
            }

            pageable = new SimplePage<>(++page, 100);
            runningJobs = logDao.findRunningJob(null, HandleCodeEnum.RUNNING.getCode(), pageable);
        }
    }
}
