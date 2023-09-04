package cn.aulang.job.admin.service;

import cn.aulang.job.admin.model.po.JobHandlerParam;
import cn.aulang.job.admin.model.po.JobHandlerRegistry;
import cn.aulang.job.admin.model.po.JobInfo;
import cn.aulang.job.admin.model.po.JobLog;
import cn.aulang.job.core.common.Constants;
import cn.aulang.job.core.enums.HandleCodeEnum;
import cn.aulang.job.core.enums.TriggerCodeEnum;
import cn.aulang.job.core.model.CallbackParam;
import cn.aulang.job.core.model.HandlerRegisterParam;
import cn.aulang.job.core.model.RegisterHandler;
import cn.aulang.job.core.model.RegisterParam;
import cn.aulang.job.core.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 调度器接口服务
 *
 * @author wulang
 */
@Slf4j
@Service
public class JobAdminService {

    private final JobLogService logService;
    private final JobInfoService jobService;
    private final MailSendService mailSendService;
    private final JobRegistryService registryService;
    private final JobExecutorService executorService;
    private final JobHandlerParamService handlerParamService;
    private final JobHandlerRegistryService handlerRegistryService;

    @Autowired
    public JobAdminService(JobLogService logService,
                           JobInfoService jobService,
                           MailSendService mailSendService,
                           JobRegistryService registryService,
                           JobExecutorService executorService,
                           JobHandlerParamService handlerParamService,
                           JobHandlerRegistryService handlerRegistryService) {
        this.logService = logService;
        this.jobService = jobService;
        this.mailSendService = mailSendService;
        this.registryService = registryService;
        this.executorService = executorService;
        this.handlerParamService = handlerParamService;
        this.handlerRegistryService = handlerRegistryService;
    }

    public Response<String> register(RegisterParam param) {
        if (StringUtils.isAnyBlank(param.getType(), param.getAppName(), param.getAddress())) {
            return Response.fail("Arguments: [type, appName, address] can not be blank");
        }

        try {
            boolean register = registryService.register(param.getType(), param.getAppName(), param.getAddress());
            if (register) {
                registerExecutor(param.getAppName(), param.getAppTitle(), param.getGlueTypes());
            }
        } catch (Exception e) {
            log.error("Executor register/beat fail", e);
            return Response.fail(e.getMessage());
        }

        return Response.success();
    }

    public Response<String> callback(CallbackParam param) {
        JobLog jobLog = logService.get(param.getLogId());
        JobInfo jobInfo = jobService.get(param.getJobId());

        Date date = new Date();
        if (jobLog == null) {
            jobLog = new JobLog();
            jobLog.setId(param.getLogId());
            jobLog.setJobId(param.getJobId());
            jobLog.setExecutorId(jobInfo.getExecutorId());
            jobLog.setTriggerTime(date);
            jobLog.setTriggerCode(TriggerCodeEnum.SUCCESS.getCode());
            jobLog.setHandleTime(date);
            jobLog.setHandleCode(param.getHandleCode());
            jobLog.setHandleMsg(param.getHandleMsg());

            try {
                logService.saveHandleInfo(jobLog);
            } catch (Exception e) {
                log.error("Executor callback fail", e);
                return Response.fail(e.getMessage());
            }

            return Response.success();
        }

        StringBuilder handleMsg = new StringBuilder();
        if (StringUtils.isNotBlank(jobLog.getHandleMsg())) {
            handleMsg.append(jobLog.getHandleMsg());
        }

        if (StringUtils.isNotBlank(param.getHandleMsg())) {
            handleMsg.append(Constants.CRLF).append(param.getHandleMsg());
        }

        jobLog.setHandleTime(date);
        jobLog.setHandleMsg(handleMsg.toString());
        jobLog.setHandleCode(param.getHandleCode());

        try {
            logService.save(jobLog);

            if (HandleCodeEnum.SUCCESS.getCode() != param.getHandleCode()) {
                mailSendService.sendExeFail(jobInfo);
            }
        } catch (Exception e) {
            log.error("Executor callback fail", e);
            return Response.fail(e.getMessage());
        }

        return Response.success();
    }

    public Response<String> unregister(RegisterParam param) {
        if (StringUtils.isAnyBlank(param.getType(), param.getAppName(), param.getAddress())) {
            return Response.fail("Arguments: [type, appName, address] can not be blank");
        }

        try {
            int ret = registryService.deleteRegistry(
                    param.getType(),
                    param.getAppName(),
                    param.getAddress());

            if (ret > 0) {
                // 取消节点所有运行中的任务
                logService.killRunningJobByAddress(param.getAddress(), "Executor unregister");
            }
        } catch (Exception e) {
            log.error("Executor unregister fail", e);
            return Response.fail(e.getMessage());
        }

        return Response.success();
    }

    public Response<String> registerHandler(HandlerRegisterParam param) {
        String appName = param.getAppName();

        if (StringUtils.isBlank(appName)) {
            return Response.fail("appName can not be blank");
        }

        List<RegisterHandler> registerHandlers = param.getHandlers();
        if (CollectionUtils.isEmpty(registerHandlers)) {
            // 删除所有
            handlerParamService.deleteByAppName(appName);
            handlerRegistryService.deleteByAppName(appName);
            return Response.success();
        }

        Date updateTime = new Date();

        List<JobHandlerRegistry> handlers = registerHandlers.stream()
                .map(e -> new JobHandlerRegistry(appName, e, updateTime))
                .collect(Collectors.toList());

        List<JobHandlerParam> fields = registerHandlers.stream()
                .flatMap(handler -> {
                    if (!CollectionUtils.isEmpty(handler.getParamFields())) {
                        return handler.getParamFields().stream()
                                .map(field -> new JobHandlerParam(appName, handler.getName(), field, updateTime));
                    } else {
                        return Stream.empty();
                    }
                })
                .collect(Collectors.toList());

        handlerRegistryService.refreshRegister(appName, handlers, updateTime);
        handlerParamService.refreshRegister(appName, fields, updateTime);

        return Response.success();
    }

    private void registerExecutor(String appName, String appTitle, String glueTypes) {
        executorService.register(appName, appTitle, glueTypes);
    }

    public List<String> addresses() {
        return registryService.findHealthAdminAddress();
    }
}
