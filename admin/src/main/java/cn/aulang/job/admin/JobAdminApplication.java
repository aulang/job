package cn.aulang.job.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.mongo.MongoMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

/**
 * 任务调度器启动器
 *
 * @author wulang
 */
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoMetricsAutoConfiguration.class})
public class JobAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobAdminApplication.class, args);
    }
}
