package cn.aulang.job.executor;

import cn.aulang.job.config.JobProperties;
import cn.aulang.job.core.common.Constants;
import cn.aulang.job.core.log.JobFileAppender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.DisposableBean;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 日志清除执行器
 *
 * @author wulang
 */
@Slf4j
public class CleanLogExecutor implements DisposableBean {

    protected final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(
            Thread.ofVirtual().name("JobCleanLogExecutor-").factory());

    protected int logRetentionDays;

    public CleanLogExecutor(JobProperties properties) {
        // 初始化日志
        JobFileAppender.initLogPath(properties.getLogPath());

        this.logRetentionDays = properties.getLogRetentionDays();

        // 小于0不开启清理
        if (logRetentionDays > 0) {
            // 清理间隔最小7天
            logRetentionDays = Math.max(7, logRetentionDays);

            executorService.scheduleAtFixedRate(this::clean, 0, 1, TimeUnit.DAYS);
        }
    }

    protected void clean() {
        try {
            Date date = new Date();

            Date from = DateUtils.addDays(date, -2 * logRetentionDays);
            Date to = DateUtils.addDays(date, -logRetentionDays);
            String logBasePath = JobFileAppender.getLogPath();

            while (from.before(to)) {
                delDir(logBasePath, from);
                from = DateUtils.addDays(from, 1);
            }
        } catch (IOException e) {
            log.error("Clean job log files fail!", e);
        }
    }

    protected void delDir(String baseDir, Date date) throws IOException {
        String dateStr = DateFormatUtils.format(date, Constants.DATE_PATTERN);
        String dailyLogDir = baseDir + Constants.SPLIT_DIVIDE + dateStr;

        File file = new File(dailyLogDir);
        if (file.exists() && file.isDirectory()) {
            FileUtils.deleteDirectory(file);
        }
    }

    @Override
    public void destroy() {
        executorService.shutdownNow();
    }
}
