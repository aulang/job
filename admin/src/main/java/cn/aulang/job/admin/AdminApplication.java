package cn.aulang.job.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.mongo.MongoMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 任务调度器启动器
 *
 * @author wulang
 */
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cn.aulang.job.admin.feign")
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoMetricsAutoConfiguration.class})
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
