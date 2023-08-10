package cn.aulang.job.executor;

import cn.aulang.job.config.JobProperties;
import cn.aulang.job.core.api.AdminApi;
import cn.aulang.job.core.common.Constants;
import cn.aulang.job.core.enums.RegisterTypeEnum;
import cn.aulang.job.core.model.RegisterParam;
import cn.aulang.job.core.model.Response;
import cn.aulang.job.thread.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.util.ResourceUtils;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 注册和心跳执行器
 *
 * @author wulang
 */
@Slf4j
public class RegisterExecutor implements ApplicationContextAware, InitializingBean, DisposableBean {

    private static final String HTTP = "http://";
    private static final String PORT = "server.port";
    private static final Integer DEFAULT_PORT = 8080;
    private static final String CONTEXT_PATH = "server.servlet.context-path";


    protected final AdminApi adminApi;
    protected final JobProperties properties;

    protected RegisterParam param;
    protected ApplicationContext applicationContext;

    protected final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder().setNameFormat("JobExecutorBeatThread-%d").build());

    public RegisterExecutor(AdminApi adminApi, JobProperties properties) {
        this.adminApi = adminApi;
        this.properties = properties;
    }

    @Override
    public void destroy() {
        executorService.shutdownNow();
        unregister();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 构造注册参数
        param = buildRegistryParam();
        // 注册处理器，执行一次即可
        new JobHandlerRegister(applicationContext, adminApi, properties.getAccessToken(), param.getAppName()).register();

        // 心跳间隔最小10s
        int interval = Math.max(10, properties.getBeatInterval());
        // 启动注册和心跳线程
        executorService.scheduleWithFixedDelay(this::beat, 0, interval, TimeUnit.SECONDS);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void beat() {
        Response<String> result = adminApi.register(param, properties.getAccessToken());
        if (!result.isSuccess()) {
            log.error("Executor register fail: {}", result.getMessage());
        }
    }

    public void unregister() {
        Response<String> result = adminApi.unregister(param, properties.getAccessToken());
        if (!result.isSuccess()) {
            log.error("Executor unregister fail: {}", result.getMessage());
        }
    }

    private String getApplicationName() {
        if (StringUtils.isNotBlank(properties.getAppName())) {
            return properties.getAppName();
        } else {
            return applicationContext.getEnvironment().getProperty("spring.application.name");
        }
    }

    private String getRegisterUrl() throws UnknownHostException {
        String url = properties.getUrl();

        if (StringUtils.isNotBlank(url) && ResourceUtils.isUrl(url)) {
            return url;
        }

        if (StringUtils.isNotBlank(properties.getIp()) && properties.getPort() != null) {
            return HTTP + properties.getIp() + Constants.SPLIT_COLON + properties.getPort();
        }

        String ip = Inet4Address.getLocalHost().getHostAddress();
        Integer port = applicationContext.getEnvironment().getProperty(PORT, Integer.class, DEFAULT_PORT);
        String contextPath = applicationContext.getEnvironment().getProperty(CONTEXT_PATH, Constants.STRING_BLANK);

        return HTTP + ip + Constants.SPLIT_COLON + port + Constants.SPLIT_DIVIDE + contextPath;
    }

    private RegisterParam buildRegistryParam() throws UnknownHostException {
        String appTitle = properties.getAppTitle();

        if (StringUtils.isBlank(appTitle)) {
            appTitle = getApplicationName();
        }

        return new RegisterParam(
                RegisterTypeEnum.EXECUTOR.name(),
                getApplicationName(),
                appTitle,
                getRegisterUrl(),
                properties.getGlueTypes()
        );
    }
}
