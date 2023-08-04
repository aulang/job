package cn.aulang.job.core.context;

import cn.aulang.job.core.common.Constants;
import cn.aulang.job.core.enums.HandleCodeEnum;
import cn.aulang.job.core.log.ClassNameAbbreviator;
import cn.aulang.job.core.log.JobFileAppender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.helpers.MessageFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

/**
 * Job帮助器
 *
 * @author wulang
 */
@Slf4j
public class JobHelper {

    private static final ClassNameAbbreviator abbreviator = new ClassNameAbbreviator();

    /**
     * 获取任务ID
     *
     * @return 任务ID
     */
    public static long getJobId() {
        JobContext jobContext = JobContext.getJobContext();
        if (jobContext == null) {
            return -1;
        }

        return jobContext.getJobId();
    }

    /**
     * 获取任务运行日志ID
     *
     * @return 日志ID
     */
    public static long getLogId() {
        JobContext jobContext = JobContext.getJobContext();
        if (jobContext == null) {
            return -1;
        }

        return jobContext.getLogId();
    }

    /**
     * 获取任务参数
     *
     * @return 任务参数
     */
    public static String getJobParam() {
        JobContext jobContext = JobContext.getJobContext();
        if (jobContext == null) {
            return null;
        }

        return jobContext.getJobParam();
    }

    /**
     * 获取任务日志文件名称
     *
     * @return 任务日志文件名称
     */
    public static String getJobLogFileName() {
        JobContext jobContext = JobContext.getJobContext();
        if (jobContext == null) {
            return null;
        }

        return jobContext.getJobLogFileName();
    }

    /**
     * 获取日志分片索引
     *
     * @return 日志分片索引
     */
    public static int getShardIndex() {
        JobContext jobContext = JobContext.getJobContext();
        if (jobContext == null) {
            return -1;
        }

        return jobContext.getShardIndex();
    }

    /**
     * 获取分片总数
     *
     * @return 分片总数
     */
    public static int getShardTotal() {
        JobContext jobContext = JobContext.getJobContext();
        if (jobContext == null) {
            return -1;
        }

        return jobContext.getShardTotal();
    }

    /**
     * 追加日志
     *
     * @param append 日志格式 like "xxx {} xxx {} xxx"
     * @param args   日志参数 "abc, true"
     * @return 追加结果
     */
    public static boolean log(String append, Object... args) {
        String appendLog = MessageFormatter.arrayFormat(append, args).getMessage();
        StackTraceElement callInfo = new Throwable().getStackTrace()[1];
        return logDetail(callInfo, appendLog);
    }

    /**
     * 追加异常日志
     *
     * @param e 异常信息
     * @return 追加结果
     */
    public static boolean log(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String appendLog = sw.toString();

        StackTraceElement callInfo = new Throwable().getStackTrace()[1];
        return logDetail(callInfo, appendLog);
    }

    /**
     * 追加日志
     *
     * @param callInfo  调用堆栈
     * @param appendLog 日志内容
     */
    private static boolean logDetail(StackTraceElement callInfo, String appendLog) {
        JobContext jobContext = JobContext.getJobContext();
        if (jobContext == null) {
            return false;
        }

        appendLog = (appendLog != null ? appendLog : Constants.STRING_BLANK);

        String formatAppendLog = DateFormatUtils.format(new Date(), Constants.DATETIME_PATTERN)
                + Constants.SPACE
                + Constants.LEFT_BRACKET
                + abbreviator.abbreviate(callInfo.getClassName())
                + Constants.SPLIT_SHARP
                + callInfo.getMethodName()
                + Constants.RIGHT_BRACKET
                + Constants.SPLIT_HYPHEN
                + Constants.LEFT_BRACKET
                + callInfo.getLineNumber()
                + Constants.RIGHT_BRACKET
                + Constants.SPLIT_HYPHEN
                + Constants.LEFT_BRACKET
                + Thread.currentThread().getName()
                + Constants.RIGHT_BRACKET
                + Constants.SPACE
                + appendLog;

        String logFileName = jobContext.getJobLogFileName();

        if (StringUtils.isNotBlank(logFileName)) {
            JobFileAppender.appendLog(logFileName, formatAppendLog);
            return true;
        } else {
            log.info(formatAppendLog);
            return false;
        }
    }


    /**
     * 任务执行成功
     *
     * @return 处理结果
     */
    public static boolean handleSuccess() {
        return handleResult(HandleCodeEnum.SUCCESS.getCode(), HandleCodeEnum.SUCCESS.getMsg());
    }

    /**
     * 任务执行成功
     *
     * @param handleMsg 成功信息
     * @return 处理结果
     */
    public static boolean handleSuccess(String handleMsg) {
        return handleResult(HandleCodeEnum.SUCCESS.getCode(), handleMsg);
    }

    /**
     * 任务执行失败
     *
     * @return 处理结果
     */
    public static boolean handleFail() {
        return handleResult(HandleCodeEnum.FAIL.getCode(), HandleCodeEnum.FAIL.getMsg());
    }

    /**
     * 任务执行失败
     *
     * @param handleMsg 失败信息
     * @return 处理结果
     */
    public static boolean handleFail(String handleMsg) {
        return handleResult(HandleCodeEnum.FAIL.getCode(), handleMsg);
    }

    /**
     * 任务执行超时
     *
     * @return 处理结果
     */
    public static boolean handleTimeout() {
        return handleResult(HandleCodeEnum.TIMEOUT.getCode(), HandleCodeEnum.TIMEOUT.getMsg());
    }

    /**
     * 任务执行超时
     *
     * @param handleMsg 超时信息
     * @return 处理结果
     */
    public static boolean handleTimeout(String handleMsg) {
        return handleResult(HandleCodeEnum.TIMEOUT.getCode(), handleMsg);
    }

    /**
     * 任务执行结果
     *
     * @param handleCode 结果状态码
     * @param handleMsg  处理信息
     * @return 处理结果
     * @see HandleCodeEnum
     */
    public static boolean handleResult(int handleCode, String handleMsg) {
        JobContext jobContext = JobContext.getJobContext();
        if (jobContext == null) {
            return false;
        }

        jobContext.setHandleCode(handleCode);
        if (handleMsg != null) {
            jobContext.setHandleMsg(handleMsg);
        }
        return true;
    }
}
