package cn.aulang.job.admin.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 调度器自动装配
 *
 * @author wulang
 */
@Configuration
@EnableConfigurationProperties(JobProperties.class)
public class JobAdminAutoConfig {
}
