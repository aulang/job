package cn.aulang.job.admin.service;

import cn.aulang.job.admin.client.ExecutorClient;
import cn.aulang.job.admin.config.JobProperties;
import cn.aulang.job.admin.enums.BlockStrategyEnum;
import cn.aulang.job.admin.enums.MisfireStrategyEnum;
import cn.aulang.job.admin.enums.RouteStrategyEnum;
import cn.aulang.job.admin.enums.TriggerTypeEnum;
import cn.aulang.job.admin.model.dto.DataXIncrDTO;
import cn.aulang.job.admin.model.po.JobExecutor;
import cn.aulang.job.admin.model.po.JobGlueCode;
import cn.aulang.job.admin.model.po.JobInfo;
import cn.aulang.job.admin.model.po.JobLog;
import cn.aulang.job.admin.router.ExecutorRouter;
import cn.aulang.job.admin.router.impl.BusyOverExecutorRouter;
import cn.aulang.job.admin.router.impl.ConsistentHashExecutorRouter;
import cn.aulang.job.admin.router.impl.FailoverExecutorRouter;
import cn.aulang.job.admin.router.impl.FirstExecutorRouter;
import cn.aulang.job.admin.router.impl.LastExecutorRouter;
import cn.aulang.job.admin.router.impl.LfuExecutorRouter;
import cn.aulang.job.admin.router.impl.LruExecutorRouter;
import cn.aulang.job.admin.router.impl.RandomExecutorRouter;
import cn.aulang.job.admin.router.impl.RoundExecutorRouter;
import cn.aulang.job.admin.scheduler.JobScheduleHelper;
import cn.aulang.job.core.common.Constants;
import cn.aulang.job.core.enums.GlueTypeEnum;
import cn.aulang.job.core.enums.HandleCodeEnum;
import cn.aulang.job.core.enums.TriggerCodeEnum;
import cn.aulang.job.core.model.DataXParam;
import cn.aulang.job.core.model.IdleBeatParam;
import cn.aulang.job.core.model.KillParam;
import cn.aulang.job.core.model.Response;
import cn.aulang.job.core.model.TriggerParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * 任务调度服务
 *
 * @author wulang
 */
@Service
public class TriggerService {

    private static final Logger logger = LoggerFactory.getLogger(TriggerService.class);

    private final JobLogService logService;
    private final JobInfoService jobService;
    private final MailSendService mailSendService;
    private final JobRegistryService registryService;
    private final JobExecutorService executorService;
    private final JobGlueCodeService glueCodeService;
    private final JobDataXParamService dataXParamService;

    private final JobProperties properties;

    @Autowired
    public TriggerService(JobInfoService jobService,
                          JobLogService logService,
                          MailSendService mailSendService,
                          JobRegistryService registryService,
                          JobExecutorService executorService,
                          JobGlueCodeService glueCodeService,
                          JobDataXParamService dataXParamService,
                          JobProperties properties) {
        this.jobService = jobService;
        this.logService = logService;
        this.mailSendService = mailSendService;
        this.registryService = registryService;
        this.executorService = executorService;
        this.glueCodeService = glueCodeService;
        this.dataXParamService = dataXParamService;

        this.properties = properties;
    }

    /**
     * 自动触发
     *
     * @param jobInfo 任务
     */
    public void trigger(JobInfo jobInfo) {
        trigger(jobInfo, TriggerTypeEnum.TIMER);
    }

    /**
     * 手动触发
     *
     * @param jobInfo       任务
     * @param executorParam 任务参数
     * @param addresses     手动录入地址
     */
    public void trigger(JobInfo jobInfo, String executorParam, List<String> addresses) {
        JobExecutor executor = executorService.get(jobInfo.getExecutorId());
        if (executor == null) {
            logger.error("Executor id: {} not exists", jobInfo.getExecutorId());
            return;
        }

        // 手动调用设置当前次触发时间
        jobInfo.setTriggerLastTime(System.currentTimeMillis());
        trigger(jobInfo, executor, executorParam, addresses, TriggerTypeEnum.MANUAL);
    }

    private void trigger(JobInfo jobInfo, TriggerTypeEnum triggerType) {
        JobExecutor executor = executorService.get(jobInfo.getExecutorId());
        if (executor == null) {
            logger.error("Executor id: {} not exists", jobInfo.getExecutorId());
            return;
        }

        // 过期策略
        MisfireStrategyEnum misfireStrategy = MisfireStrategyEnum.match(jobInfo.getMisfireStrategy(), MisfireStrategyEnum.DO_NOTHING);
        // 当前次触发时间
        Long triggerCurrentTime = jobInfo.getTriggerLastTime();
        if (triggerCurrentTime != null && triggerCurrentTime < (System.currentTimeMillis() - 2 * JobScheduleHelper.PRE_READ_MILLISECONDS)) {
            // 过期的调度，忽略策略，不做任何事丢弃
            if (misfireStrategy == MisfireStrategyEnum.DO_NOTHING) {
                logger.warn("Misfire job id: {} do nothing!", jobInfo.getId());
                return;
            }
            // 过期补偿
            triggerType = TriggerTypeEnum.MISFIRE;
        }


        BlockStrategyEnum blockStrategy = BlockStrategyEnum.match(jobInfo.getBlockStrategy(), BlockStrategyEnum.SERIAL_EXECUTION);

        List<JobLog> runningJobs = logService.findRunningJobWithExecutorAddress(jobInfo.getId());

        // 空闲机器地址
        List<String> addresses = null;

        if (blockStrategy == BlockStrategyEnum.SERIAL_EXECUTION) {
            // 单机串行
            if (checkRunningJobs(runningJobs)) {
                logger.warn("Job id: {} have running instance, skip this trigger!", jobInfo.getId());
                return;
            }
        } else if (blockStrategy == BlockStrategyEnum.PARALLEL_EXECUTION) {
            // 多机并行
            addresses = registryService.findHealthExecutorAddress(executor.getAppName());
            if (!CollectionUtils.isEmpty(runningJobs)) {
                List<String> runningAddresses = runningJobs.parallelStream()
                        .map(JobLog::getExecutorAddress)
                        .filter(StringUtils::isNotBlank)
                        .toList();

                addresses.removeAll(runningAddresses);
            }

            // 没有空闲的执行器
            if (CollectionUtils.isEmpty(addresses)) {
                logger.warn("Job id: {} executor has no idle instance, skip this trigger!", jobInfo.getId());
                return;
            }
        } else if (blockStrategy == BlockStrategyEnum.DISCARD_LATER) {
            // 丢弃后续调度
            if (checkRunningJobs(runningJobs)) {
                logger.warn("Job id: {} has running instance, drop this trigger!", jobInfo.getId());
                return;
            }
        } else if (blockStrategy == BlockStrategyEnum.COVER_EARLY) {
            // 覆盖之前调度
            logger.warn("Job id: {} has running instance, kill early running job!", jobInfo.getId());
            // 杀死正在运行的
            killRunningJobs(runningJobs);
        }

        trigger(jobInfo, executor, null, addresses, triggerType);
    }

    private void trigger(JobInfo jobInfo,
                         JobExecutor executor,
                         String executorParam,
                         List<String> addresses,
                         TriggerTypeEnum triggerType) {
        String appName = executor.getAppName();

        if (CollectionUtils.isEmpty(addresses)) {
            addresses = registryService.findHealthExecutorAddress(executor.getAppName());
        }

        if (CollectionUtils.isEmpty(addresses)) {
            logger.error("Executor: {} no instance available", appName);
            logService.saveFailLog(jobInfo, triggerType, null, "Executor no instance available");
            mailSendService.sendTriggerFail(jobInfo);
            return;
        }

        if (StringUtils.isNotBlank(executorParam)) {
            jobInfo.setExecutorParam(executorParam);
        }

        // 要失败重试需大于0
        int failRetry = (jobInfo.getFailRetry() != null ? jobInfo.getFailRetry() : 0);
        if (failRetry < 0) {
            failRetry = 0;
        }

        TriggerParam param;
        DataXIncrDTO incrDTO = new DataXIncrDTO();
        try {
            param = buildTriggerParam(jobInfo, incrDTO);
        } catch (Exception e) {
            logService.saveFailLog(jobInfo, triggerType, null, e.getMessage());
            logger.error("Fail to build TriggerParam", e);
            mailSendService.sendTriggerFail(jobInfo);
            return;
        }

        RouteStrategyEnum routeStrategy = RouteStrategyEnum.match(jobInfo.getRouteStrategy(), RouteStrategyEnum.RANDOM);
        if (routeStrategy == RouteStrategyEnum.BROADCAST) {
            // 广播模式，所有节点调度一次
            int size = addresses.size();
            param.setShardTotal(size);

            for (int i = 0; i < size; i++) {
                param.setShardIndex(i);
                processTrigger(jobInfo, executor, addresses, param, incrDTO, routeStrategy, triggerType, failRetry,
                        param.getShardIndex(), param.getShardTotal());
            }
        } else {
            // 非广播模式，调度一次
            processTrigger(jobInfo, executor, addresses, param, incrDTO, routeStrategy, triggerType, failRetry,
                    param.getShardIndex(), param.getShardTotal());
        }
    }

    private boolean checkRunningJobs(List<JobLog> runningJobs) {
        if (CollectionUtils.isEmpty(runningJobs)) {
            return false;
        }

        ExecutorClient executorClient = new ExecutorClient();
        for (JobLog jobLog : runningJobs) {
            executorClient.setAddress(jobLog.getExecutorAddress());

            Response<String> result = executorClient.idleBeat(new IdleBeatParam(jobLog.getJobId()), properties.getAccessToken());

            if (!result.isSuccess() || result.isNetError()) {
                // 非网络异常返回失败，就是节点正在运行当前任务
                return true;
            }
        }

        return false;
    }

    private void killRunningJobs(List<JobLog> runningJobs) {
        if (CollectionUtils.isEmpty(runningJobs)) {
            return;
        }

        ExecutorClient executorClient = new ExecutorClient();
        for (JobLog jobLog : runningJobs) {
            executorClient.setAddress(jobLog.getExecutorAddress());

            executorClient.kill(new KillParam(jobLog.getJobId(), "Block strategy cover early!"), properties.getAccessToken());

            jobLog.setHandleTime(new Date());
            jobLog.setHandleCode(HandleCodeEnum.CANCEL.getCode());

            String handleMsg = jobLog.getHandleMsg() != null ? (jobLog.getHandleMsg() + Constants.CRLF) : StringUtils.EMPTY;
            jobLog.setHandleMsg(handleMsg + "Job be Killed, reason: Block strategy cover early!");

            logService.update(jobLog);
        }
    }

    private TriggerParam buildTriggerParam(JobInfo jobInfo, DataXIncrDTO incrDTO) throws Exception {
        TriggerParam param = new TriggerParam();

        GlueTypeEnum glueType = GlueTypeEnum.match(jobInfo.getGlueType());

        param.setJobId(jobInfo.getId());
        param.setGlueType(jobInfo.getGlueType());

        if (glueType == GlueTypeEnum.BEAN) {
            // 设置处理器和参数
            param.setHandler(jobInfo.getExecutorHandler());
            param.setHandlerParam(jobInfo.getExecutorParam());
        } else if (glueType == GlueTypeEnum.DATAX) {
            // 构建DataX参数
            DataXParam dataXParam = dataXParamService.buildDataXParam(jobInfo, incrDTO);
            String handlerParam = JobDataXParamService.JSON_MAPPER.toJson(dataXParam);
            param.setHandler(glueType.getName());
            param.setHandlerParam(handlerParam);
        } else if (glueType.isScript()) {
            // 获取和设置任务脚本代码
            JobGlueCode code = glueCodeService.getByJobId(jobInfo.getId());
            param.setGlueSource(code.getGlueSource());
        }

        // 超时时间
        Integer timeout = jobInfo.getTimeout();
        if (timeout != null && timeout > 0) {
            param.setTimeout(timeout);
        }

        param.setShardIndex(0);
        param.setShardTotal(1);

        return param;
    }

    private void processTrigger(JobInfo jobInfo, JobExecutor executor, List<String> addresses, TriggerParam param, DataXIncrDTO incrDTO,
                                RouteStrategyEnum routeStrategy, TriggerTypeEnum triggerType, int failRetry, int index, int total) {
        String shardingParam = index + Constants.SPLIT_DIVIDE + total;
        JobLog jobLog = logService.newJobLog(jobInfo, triggerType, shardingParam, failRetry);

        param.setLogId(jobLog.getId());
        param.setLogDateTime(jobLog.getTriggerTime().getTime());

        Response<String> routeResult;
        try {
            routeResult = route(executor, param, routeStrategy, addresses, index);
        } catch (Exception e) {
            logger.error("Executor address route strategy fail", e);
            routeResult = Response.fail(e.getMessage());
        }

        String address = null;
        if (routeResult.isSuccess()) {
            address = routeResult.getData();
        }

        logger.info("Start trigger job, job info: {}", jobInfo);

        Response<String> triggerResult;
        if (address != null) {
            triggerResult = executorRun(param, address);
        } else {
            triggerResult = routeResult;
        }

        jobLog.setExecutorAddress(address);
        jobLog.setTriggerMsg(triggerResult.getMsg());

        if (triggerResult.isSuccess()) {
            jobLog.setTriggerCode(TriggerCodeEnum.SUCCESS.getCode());
            jobLog.setHandleCode(HandleCodeEnum.RUNNING.getCode());

            dataXParamService.refreshIncrStartValue(incrDTO);
        } else {
            jobLog.setTriggerCode(TriggerCodeEnum.FAIL.getCode());
            mailSendService.sendTriggerFail(jobInfo);
        }

        logService.saveTriggerInfo(jobLog);

        logger.info("End trigger job, job info: {}", jobInfo);

        if (address != null && !triggerResult.isSuccess() && failRetry > 0) {
            logger.warn("Job fail retry, job info: {}", jobInfo);
            // 失败重试
            triggerType = TriggerTypeEnum.RETRY;
            processTrigger(jobInfo, executor, addresses, param, incrDTO, routeStrategy, triggerType, --failRetry, index, total);
        }

        if (triggerResult.isSuccess()) {
            // 触发子任务
            triggerChildJob(jobInfo);
        }
    }

    private Response<String> route(JobExecutor executor, TriggerParam param, RouteStrategyEnum routeStrategy,
                                  List<String> addresses, int index) {
        if (!CollectionUtils.isEmpty(addresses)) {
            if (routeStrategy == RouteStrategyEnum.BROADCAST) {
                // 广播模式特殊处理
                if (index < addresses.size()) {
                    return Response.success(addresses.get(index));
                } else {
                    return Response.success(addresses.get(index / addresses.size()));
                }
            } else {
                ExecutorRouter executorRouter = getRouterByStrategy(routeStrategy);
                if (executorRouter != null) {
                    // 路由策略
                    return executorRouter.route(param, addresses);
                } else {
                    return Response.fail("Executor route strategy invalid");
                }
            }
        } else {
            return Response.fail("Executor: " + executor.getAppName() + " no instance available");
        }
    }

    private ExecutorRouter getRouterByStrategy(RouteStrategyEnum routeStrategy) {
        return switch (routeStrategy) {
            case FIRST -> new FirstExecutorRouter();
            case LAST -> new LastExecutorRouter();
            case ROUND -> new RoundExecutorRouter();
            case RANDOM -> new RandomExecutorRouter();
            case HASH -> new ConsistentHashExecutorRouter();
            case LFU -> new LfuExecutorRouter();
            case LRU -> new LruExecutorRouter();
            case FAILOVER -> new FailoverExecutorRouter(properties.getAccessToken());
            case BUSY_OVER -> new BusyOverExecutorRouter(properties.getAccessToken());
            default -> null;
        };
    }

    private void triggerChildJob(JobInfo jobInfo) {
        Long parentId = jobInfo.getId();

        List<JobInfo> childJobs = jobService.findChildJobs(parentId);

        if (CollectionUtils.isEmpty(childJobs)) {
            return;
        }

        for (JobInfo childJob : childJobs) {
            logger.info("Trigger job child, parent job:{},  child job info: {}", parentId, childJob);
            childJob.setTriggerLastTime(System.currentTimeMillis());
            trigger(childJob, TriggerTypeEnum.PARENT);
        }
    }

    private Response<String> executorRun(TriggerParam param, String address) {
        try {
            ExecutorClient client = new ExecutorClient(address);
            return client.run(param, properties.getAccessToken());
        } catch (Exception e) {
            logger.error("Job trigger error, please check if the executor[" + address + "] is running", e);
            return Response.fail(e.getMessage());
        }
    }
}