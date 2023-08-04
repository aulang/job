package cn.aulang.job.config;

import cn.aulang.job.client.LoadBalancerAdminClient;
import cn.aulang.job.client.RetryAdminClient;
import cn.aulang.job.core.api.AdminApi;
import cn.aulang.job.endpoint.ExecutorEndpoint;
import cn.aulang.job.executor.CallbackExecutor;
import cn.aulang.job.executor.CleanLogExecutor;
import cn.aulang.job.executor.JobHandlerExecutor;
import cn.aulang.job.executor.RegisterExecutor;
import cn.aulang.job.service.JobExecutorService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wulang
 */
@Configuration
@EnableConfigurationProperties(JobProperties.class)
@ConditionalOnProperty(value = "al-job.enabled", matchIfMissing = true)
public class JobExecutorAutoConfig {

    @Bean
    @ConditionalOnMissingBean(AdminApi.class)
    public AdminApi adminClient(JobProperties properties) {
        if (properties.isLoadBalance()) {
            return new LoadBalancerAdminClient(properties);
        } else {
            return new RetryAdminClient(properties.getAdminUrl(), properties.getRetry());
        }
    }

    @Bean
    @ConditionalOnMissingBean(RegisterExecutor.class)
    public RegisterExecutor registryExecutor(AdminApi adminApi, JobProperties properties) {
        return new RegisterExecutor(adminApi, properties);
    }

    @Bean
    @ConditionalOnMissingBean(CleanLogExecutor.class)
    public CleanLogExecutor cleanLogExecutor(JobProperties properties) {
        return new CleanLogExecutor(properties);
    }

    @Bean
    @ConditionalOnMissingBean(CallbackExecutor.class)
    public CallbackExecutor callbackExecutor(AdminApi adminApi, JobProperties properties) {
        return new CallbackExecutor(adminApi, properties.getAccessToken());
    }

    @Bean
    @ConditionalOnMissingBean(JobHandlerExecutor.class)
    public JobHandlerExecutor jobHandlerExecutor() {
        return new JobHandlerExecutor();
    }

    @Bean
    @ConditionalOnMissingBean(JobExecutorService.class)
    public JobExecutorService jobExecutorService(JobHandlerExecutor jobHandlerExecutor, CallbackExecutor callbackExecutor) {
        return new JobExecutorService(jobHandlerExecutor, callbackExecutor);
    }

    @Bean
    @ConditionalOnMissingBean(ExecutorEndpoint.class)
    public ExecutorEndpoint executorEndpoint(JobProperties properties, JobExecutorService executorService) {
        return new ExecutorEndpoint(properties, executorService);
    }
}
