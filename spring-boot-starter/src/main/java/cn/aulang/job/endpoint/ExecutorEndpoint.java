package cn.aulang.job.endpoint;

import cn.aulang.job.config.JobProperties;
import cn.aulang.job.core.api.ExecutorApi;
import cn.aulang.job.core.common.Constants;
import cn.aulang.job.core.model.IdleBeatParam;
import cn.aulang.job.core.model.KillParam;
import cn.aulang.job.core.model.LogParam;
import cn.aulang.job.core.model.LogResult;
import cn.aulang.job.core.model.Response;
import cn.aulang.job.core.model.TriggerParam;
import cn.aulang.job.service.JobExecutorService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 执行器和调度器通讯端点
 *
 * @author wulang
 */
@RestController
@RequestMapping("/job-executor")
public class ExecutorEndpoint implements ExecutorApi {

    private final JobProperties properties;
    private final JobExecutorService executorService;

    @Autowired
    public ExecutorEndpoint(JobProperties properties, JobExecutorService executorService) {
        this.properties = properties;
        this.executorService = executorService;
    }

    private boolean incorrectAccessToken(String accessToken) {
        String trueToken = properties.getAccessToken();
        return StringUtils.isNotBlank(trueToken) && !trueToken.equals(accessToken);
    }

    @Override
    @PostMapping("/beat")
    public Response<String> beat(
            @RequestHeader(name = Constants.ACCESS_TOKEN_HEADER, required = false) String accessToken) {
        if (incorrectAccessToken(accessToken)) {
            return Response.fail("Incorrect access token!");
        }

        return executorService.beat();
    }

    @Override
    @PostMapping("/running")
    public Response<Integer> running(String accessToken) {
        if (incorrectAccessToken(accessToken)) {
            return Response.fail("Incorrect access token!");
        }

        return executorService.running();
    }

    @Override
    @PostMapping("/idle-beat")
    public Response<String> idleBeat(
            @RequestBody IdleBeatParam param,
            @RequestHeader(name = Constants.ACCESS_TOKEN_HEADER, required = false) String accessToken) {
        if (incorrectAccessToken(accessToken)) {
            return Response.fail("Incorrect access token!");
        }

        return executorService.idleBeat(param);
    }

    @Override
    @PostMapping("/run")
    public Response<String> run(
            @RequestBody TriggerParam param,
            @RequestHeader(name = Constants.ACCESS_TOKEN_HEADER, required = false) String accessToken) {
        if (incorrectAccessToken(accessToken)) {
            return Response.fail("Incorrect access token!");
        }

        return executorService.run(param);
    }

    @Override
    @PostMapping("/kill")
    public Response<String> kill(
            @RequestBody KillParam param,
            @RequestHeader(name = Constants.ACCESS_TOKEN_HEADER, required = false) String accessToken) {
        if (incorrectAccessToken(accessToken)) {
            return Response.fail("Incorrect access token!");
        }

        return executorService.kill(param);
    }

    @Override
    @PostMapping("/log")
    public Response<LogResult> log(
            @RequestBody LogParam param,
            @RequestHeader(name = Constants.ACCESS_TOKEN_HEADER, required = false) String accessToken) {
        if (incorrectAccessToken(accessToken)) {
            return Response.fail("Incorrect access token!");
        }

        return executorService.log(param);
    }
}
