package cn.aulang.job.service;

import cn.aulang.job.core.common.Constants;
import cn.aulang.job.core.enums.GlueTypeEnum;
import cn.aulang.job.core.handler.IJobHandler;
import cn.aulang.job.core.handler.impl.ScriptJobHandler;
import cn.aulang.job.core.log.JobFileAppender;
import cn.aulang.job.core.model.IdleBeatParam;
import cn.aulang.job.core.model.KillParam;
import cn.aulang.job.core.model.LogParam;
import cn.aulang.job.core.model.LogResult;
import cn.aulang.job.core.model.Response;
import cn.aulang.job.core.model.TriggerParam;
import cn.aulang.job.executor.CallbackExecutor;
import cn.aulang.job.executor.JobHandlerExecutor;
import cn.aulang.job.utils.ProcessUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * 执行器服务
 *
 * @author wulang
 */
public class JobExecutorService {

    /**
     * 任务处理器
     */
    private static final ConcurrentHashMap<String, IJobHandler> HANDLERS = new ConcurrentHashMap<>();

    public static void registerHandler(IJobHandler handler) {
        HANDLERS.put(handler.name(), handler);
    }

    public static void registerHandlers(List<IJobHandler> handlers) {
        handlers.forEach(JobExecutorService::registerHandler);
    }

    public static IJobHandler getHandler(String name) {
        return HANDLERS.get(name);
    }

    /**
     * 运行中任务
     */
    private static final ConcurrentHashMap<Long, RunningJob> RUNNING_JOBS = new ConcurrentHashMap<>();

    protected static void putRunningJob(TriggerParam param, Future<?> future, boolean isScript) {
        RUNNING_JOBS.put(param.getJobId(), new RunningJob(param.getJobId(), param.getLogId(), param.getLogDateTime(), isScript, future));
    }

    protected static RunningJob getRunningJob(long jobId) {
        return RUNNING_JOBS.get(jobId);
    }

    protected static RunningJob removeRunningJob(long jobId) {
        return RUNNING_JOBS.remove(jobId);
    }

    public static void finishJob(long jobId, long logId) {
        RunningJob runningJob = RUNNING_JOBS.get(jobId);
        if (runningJob != null && logId == runningJob.getLogId()) {
            RUNNING_JOBS.remove(jobId);
        }
    }

    /**
     * 设置运行中任务属性
     * <p>
     * 如脚本任务需要设置脚本进程ID
     * <p>
     * 杀死脚本任务时需要结束Java线程同时要杀死脚本进程
     *
     * @param jobId 任务ID
     * @param logId 日志ID
     * @param key   属性键
     * @param value 属性值
     * @return 是否设置成功
     */
    public static boolean setRunningJobAttribute(long jobId, long logId, String key, Object value) {
        RunningJob runningJob = RUNNING_JOBS.get(jobId);

        if (runningJob != null && logId == runningJob.getLogId()) {
            return runningJob.setAttribute(key, value);
        }

        return false;
    }

    /**
     * 获取运行中任务属性值
     * <p>
     * 如杀死脚本任务需要获取脚本进程ID
     * <p>
     * 杀死脚本任务时需要结束Java线程同时要杀死脚本进程
     *
     * @param jobId 任务ID
     * @param logId 日志ID
     * @param key   属性键
     * @param <T>   属性值类型
     * @return 属性值
     */
    public static <T> T getRunningJobAttribute(long jobId, long logId, String key) {
        RunningJob runningJob = RUNNING_JOBS.get(jobId);

        if (runningJob != null && logId == runningJob.getLogId()) {
            return runningJob.getAttribute(key);
        }

        return null;
    }


    /**
     * 任务执行线程池
     */
    protected final JobHandlerExecutor jobHandlerExecutor;
    /**
     * 任务执行回调线程池
     */
    protected final CallbackExecutor callbackExecutor;

    public JobExecutorService(JobHandlerExecutor jobHandlerExecutor, CallbackExecutor callbackExecutor) {
        this.jobHandlerExecutor = jobHandlerExecutor;
        this.callbackExecutor = callbackExecutor;
    }

    /**
     * 执行器存活检查
     *
     * @return 响应
     */
    public Response<String> beat() {
        return Response.success();
    }

    /**
     * 执行器正在执行任务数
     *
     * @return 执行任务数
     */
    public Response<Integer> running() {
        return Response.success(RUNNING_JOBS.size());
    }

    /**
     * 任务空闲检查
     *
     * @param param 参数
     * @return 响应
     */
    public Response<String> idleBeat(IdleBeatParam param) {
        long jobId = param.getJobId();

        RunningJob runningJob = getRunningJob(jobId);
        if (runningJob == null) {
            return Response.success();
        }

        if (runningJob.getFuture().isDone()) {
            removeRunningJob(jobId);
            return Response.success();
        }

        return Response.fail();
    }

    /**
     * 任务执行
     *
     * @param param 参数
     * @return 响应
     */
    public Response<String> run(TriggerParam param) {
        long jobId = param.getJobId();
        // kill之前的线程，只有阻塞策略是覆盖之前的才有重新调度，直接kill即可
        kill(jobId, "cover by after job");

        IJobHandler jobHandler;

        boolean isScript = false;
        GlueTypeEnum glueType = GlueTypeEnum.match(param.getGlueType());
        if (glueType == GlueTypeEnum.BEAN) {
            jobHandler = getHandler(param.getHandler());
            if (jobHandler == null) {
                return Response.fail("Job handler [" + param.getHandler() + "] not exists");
            }
        } else if (glueType != null && glueType.isScript()) {
            if (StringUtils.isBlank(param.getGlueSource())) {
                return Response.fail("Job handler [" + param.getHandler() + "] glue source is blank");
            }
            isScript = true;
            jobHandler = new ScriptJobHandler(jobId, glueType, param.getGlueSource(), ScriptProcessIdHandler.getInstance());
        } else {
            return Response.fail("GlueType [" + param.getGlueType() + "] is invalid");
        }

        return execute(jobHandler, param, isScript);
    }

    protected Response<String> execute(IJobHandler jobHandler, TriggerParam param, boolean isScript) {
        Future<?> future = jobHandlerExecutor.submit(jobHandler, param, callbackExecutor);

        putRunningJob(param, future, isScript);

        return Response.success();
    }

    /**
     * 取消任务
     *
     * @param param 参数
     * @return 响应
     */
    public Response<String> kill(KillParam param) {
        return kill(param.getJobId(), param.getReason());
    }

    public Response<String> kill(long jobId, String reason) {
        RunningJob runningJob = removeRunningJob(jobId);
        if (runningJob == null) {
            return Response.success();
        }

        if (!runningJob.getFuture().isDone()) {
            // 记录Kill日志
            String logFileName = JobFileAppender.makeLogFileName(new Date(runningJob.getLogDateTime()), runningJob.getLogId());
            JobFileAppender.appendLog(logFileName, "Job received a kill signal, execution terminated, reason: " + reason);
            runningJob.getFuture().cancel(true);

            // 杀死脚本进程
            killScript(runningJob, logFileName);
        }

        return Response.success();
    }

    private void killScript(RunningJob runningJob, String logFileName) {
        if (!runningJob.isScript()) {
            return;
        }

        try {
            Long pid = getRunningJobAttribute(runningJob.getJobId(), runningJob.getLogId(), Constants.PID);
            if (pid != null) {
                String result = ProcessUtils.kill(pid);
                JobFileAppender.appendLog(logFileName, result);
            }
        } catch (Exception e) {
            JobFileAppender.appendLog(logFileName, e.getMessage());
        }
    }

    /**
     * 读取日志
     *
     * @param param 参数
     * @return 响应
     */
    public Response<LogResult> log(LogParam param) {
        String logFileName = JobFileAppender.makeLogFileName(new Date(param.getLogDateTime()), param.getLogId());
        LogResult logResult = JobFileAppender.readLog(logFileName, param.getFromLineNum(), param.getReadLineNum());
        logResult.setLogId(param.getLogId());
        return new Response<>(logResult);
    }
}
