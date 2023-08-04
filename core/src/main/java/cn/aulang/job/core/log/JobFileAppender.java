package cn.aulang.job.core.log;

import cn.aulang.job.core.common.Constants;
import cn.aulang.job.core.model.LogResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;

/**
 * 文件日志追加器
 *
 * @author wulang
 */
@Slf4j
public class JobFileAppender {

    private static final String LF = "\n";
    private static final String LOG_FILE_EXTENSION = ".log";

    /**
     * 日志文件根目录
     */
    private static String logBasePath = "/data/logs/job";
    /**
     * Glue代码目录
     */
    private static String glueSrcPath = logBasePath + "/glue/src";

    public static void initLogPath(String logPath) {
        // 初始化日志目录

        if (StringUtils.isNotBlank(logPath)) {
            logBasePath = logPath;
        }

        // 创建日志文件根目录
        File logPathDir = new File(logBasePath);
        logPathDir.mkdirs();

        logBasePath = logPathDir.getPath();

        // 创建Glue代码目录
        File glueBaseDir = new File(logPathDir, "/glue/src");
        glueBaseDir.mkdirs();
        glueSrcPath = glueBaseDir.getPath();
    }

    public static String getLogPath() {
        return logBasePath;
    }

    public static String getGlueSrcPath() {
        return glueSrcPath;
    }

    /**
     * 获取日志文件名称 "logPath/yyyy-MM-dd/9999.log"
     *
     * @param triggerDate 任务触发日期
     * @param logId       日志ID
     * @return 日志文件名称
     */
    public static String makeLogFileName(Date triggerDate, long logId) {
        File logFilePath = new File(getLogPath(), DateFormatUtils.format(triggerDate, Constants.DATE_PATTERN));
        logFilePath.mkdirs();

        return logFilePath.getPath()
                .concat(Constants.SPLIT_DIVIDE)
                .concat(String.valueOf(logId))
                .concat(LOG_FILE_EXTENSION);
    }

    /**
     * 追加日志
     *
     * @param logFileName 日志文件名
     * @param appendLog   追加日志内容
     */
    public static void appendLog(String logFileName, String appendLog) {
        if (StringUtils.isBlank(logFileName) || appendLog == null) {
            return;
        }

        try {
            FileUtils.write(new File(logFileName), appendLog + Constants.CRLF, StandardCharsets.UTF_8, true);
        } catch (IOException e) {
            log.error("Write log file: {} fail, content: {}", logFileName, appendLog);
        }
    }

    /**
     * 读取日志文件行
     *
     * @param logFileName 日志文件名称
     * @param fromLineNum 读取开始行
     * @param readlineNum 读取行数
     * @return 日志内容
     */
    public static LogResult readLog(String logFileName, int fromLineNum, int readlineNum) {
        if (StringUtils.isBlank(logFileName)) {
            return new LogResult(fromLineNum, fromLineNum, "read log fail, log file name is blank", true);
        }

        File logFile = new File(logFileName);

        if (!logFile.exists()) {
            return new LogResult(fromLineNum, fromLineNum, "read log fail, log file not exists", true);
        }

        int toLineNum = 0;
        boolean end = true;
        StringBuilder sb = new StringBuilder();
        try (LineNumberReader reader =
                     new LineNumberReader(
                             new InputStreamReader(Files.newInputStream(logFile.toPath()), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                toLineNum = reader.getLineNumber();

                if (toLineNum >= fromLineNum) {
                    sb.append(line).append(LF);
                    --readlineNum;
                }

                if (readlineNum <= 0) {
                    break;
                }
            }

            if (line != null) {
                end = false;
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        if (toLineNum == 0) {
            toLineNum = fromLineNum;
        }

        return new LogResult(fromLineNum, toLineNum, sb.toString(), end);
    }

    /**
     * 读取日志文件全部行
     *
     * @param logFile 日志文件名称
     * @return 日志内容
     */
    public static String readLines(File logFile) {
        if (logFile == null) {
            return Constants.STRING_BLANK;
        }

        try {
            return FileUtils.readFileToString(logFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Read log file: " + logFile.getAbsolutePath() + " fail", e);
            return Constants.STRING_BLANK;
        }
    }
}
