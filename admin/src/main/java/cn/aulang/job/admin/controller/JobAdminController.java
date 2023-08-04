package cn.aulang.job.admin.controller;

import cn.aulang.job.admin.config.JobProperties;
import cn.aulang.job.admin.service.JobAdminService;
import cn.aulang.job.core.api.AdminApi;
import cn.aulang.job.core.common.Constants;
import cn.aulang.job.core.model.CallbackParam;
import cn.aulang.job.core.model.HandlerRegisterParam;
import cn.aulang.job.core.model.RegisterParam;
import cn.aulang.job.core.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 调度器API
 *
 * @author wulang
 */
@Slf4j
@RestController
@RequestMapping("/admin")
public class JobAdminController implements AdminApi {

    private final JobProperties properties;
    private final JobAdminService adminService;

    @Autowired
    public JobAdminController(JobProperties properties, JobAdminService adminService) {
        this.properties = properties;
        this.adminService = adminService;
    }

    private boolean incorrectAccessToken(String accessToken) {
        String trueToken = properties.getAccessToken();
        return StringUtils.isNotBlank(trueToken) && !trueToken.equals(accessToken);
    }

    @Override
    @PostMapping("/register")
    public Response<String> register(
            @RequestBody RegisterParam param,
            @RequestHeader(name = Constants.ACCESS_TOKEN_HEADER, required = false) String accessToken) {
        log.info("Executor register/beat request: {}", param.toString());

        if (incorrectAccessToken(accessToken)) {
            return Response.fail("Incorrect access token!");
        }

        return adminService.register(param);
    }

    @Override
    @PostMapping("/callback")
    public Response<String> callback(
            @RequestBody CallbackParam param,
            @RequestHeader(name = Constants.ACCESS_TOKEN_HEADER, required = false) String accessToken) {
        log.info("Executor callback request: {}", param.toString());

        if (incorrectAccessToken(accessToken)) {
            return Response.fail("Incorrect access token!");
        }

        return adminService.callback(param);
    }

    @Override
    @PostMapping("/unregister")
    public Response<String> unregister(
            @RequestBody RegisterParam param,
            @RequestHeader(name = Constants.ACCESS_TOKEN_HEADER, required = false) String accessToken) {
        log.info("Executor unregister request: {}", param.toString());

        if (incorrectAccessToken(accessToken)) {
            return Response.fail("Incorrect access token!");
        }

        return adminService.unregister(param);
    }

    @Override
    @PostMapping("/handler")
    public Response<String> registerHandler(
            @RequestBody HandlerRegisterParam param,
            @RequestHeader(name = Constants.ACCESS_TOKEN_HEADER, required = false) String accessToken) {
        log.info("Executor register handler request: {}", param);

        if (incorrectAccessToken(accessToken)) {
            return Response.fail("Incorrect access token!");
        }

        return adminService.registerHandler(param);
    }


    @Override
    @PostMapping("/address")
    public Response<List<String>> address(@RequestHeader(name = Constants.ACCESS_TOKEN_HEADER, required = false) String accessToken) {
        if (incorrectAccessToken(accessToken)) {
            return Response.fail("Incorrect access token!");
        }

        List<String> addresses = adminService.addresses();

        return Response.success(addresses);
    }
}
