package cn.aulang.job.admin.scheduler;

import cn.aulang.common.core.concurrent.ThreadFactoryBuilder;
import cn.aulang.common.core.utils.SimpleDateUtils;
import cn.aulang.job.admin.config.JobProperties;
import cn.aulang.job.admin.service.JobLogService;
import cn.aulang.job.admin.service.JobRegistryService;
import cn.aulang.job.admin.service.JobReportService;
import cn.aulang.job.core.common.Constants;
import cn.aulang.job.core.enums.RegisterTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 属性任务报告调度器
 *
 * @author wulang
 */
@Slf4j
@Component
public class JobTimingScheduler implements ApplicationContextAware, DisposableBean {

    private static final String HTTP = "http://";
    private static final String PORT = "server.port";
    private static final Integer DEFAULT_PORT = 8080;
    private static final String CONTEXT_PATH = "server.servlet.context-path";

    private final JobProperties properties;
    private final JobLogService logService;
    private final JobReportService reportService;
    private final JobRegistryService registryService;
    private final ScheduledExecutorService schedulerExecutor;

    private String appName;
    private String address;

    @Autowired
    public JobTimingScheduler(JobProperties properties,
                              JobLogService logService,
                              JobReportService reportService,
                              JobRegistryService registryService) {
        this.properties = properties;
        this.logService = logService;
        this.reportService = reportService;
        this.registryService = registryService;

        int threadCount = 2;
        if (properties.isBeatEnabled()) {
            ++threadCount;
        }
        schedulerExecutor = Executors.newScheduledThreadPool(threadCount,
                new ThreadFactoryBuilder().setNameFormat("JobAdminTimingScheduler-%d").build());
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.appName = getApplicationName(applicationContext);

        try {
            this.address = getRegisterUrl(applicationContext);
        } catch (UnknownHostException e) {
            log.error("Failed to get localhost IP address", e);
            throw new RuntimeException(e);
        }

        if (properties.isBeatEnabled()) {
            // 心跳线程，30s
            schedulerExecutor.scheduleAtFixedRate(this::register, 0, properties.getBeatInterval(), TimeUnit.SECONDS);
        } else {
            // 无需心跳时注册一次就行
            register();
        }

        // 健康监测线程，5m
        schedulerExecutor.scheduleAtFixedRate(this::heathCheck, 30, 300, TimeUnit.SECONDS);

        // 刷新任务运行报告线程， 1d
        refreshReport();
    }


    private void register() {
        try {
            registryService.register(RegisterTypeEnum.ADMIN.name(), appName, address);
        } catch (Exception e) {
            log.error("JobAdmin beat fail", e);
        }
    }

    private void unregister() {
        try {
            registryService.deleteRegistry(RegisterTypeEnum.ADMIN.name(), appName, address);
        } catch (Exception e) {
            log.error("JobAdmin unregister fail", e);
        }
    }

    private void heathCheck() {
        try {
            registryService.heathCheck();
        } catch (Exception e) {
            log.error("Executor node heath check fail", e);
        }

        try {
            logService.heathCheck();
        } catch (Exception e) {
            log.error("Job running heath check fail", e);
        }
    }

    private void refreshReport() {
        // 00:10:00
        Date initialTime = SimpleDateUtils.offsetMinute(SimpleDateUtils.endOfDay(), 10);

        // seconds
        long initialDelay = (initialTime.getTime() - new Date().getTime()) / 1000;

        // 刷新任务运行报告
        schedulerExecutor.scheduleWithFixedDelay(reportService::refreshLogReport, initialDelay, 24 * 60 * 60, TimeUnit.SECONDS);
    }

    @Override
    public void destroy() {
        schedulerExecutor.shutdownNow();
        unregister();
    }

    private String getApplicationName(ApplicationContext applicationContext) {
        if (StringUtils.isNotBlank(properties.getAppName())) {
            return properties.getAppName();
        } else {
            return applicationContext.getId();
        }
    }

    private String getRegisterUrl(ApplicationContext applicationContext) throws UnknownHostException {
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
}
