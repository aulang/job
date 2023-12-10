package cn.aulang.job.executor;

import cn.aulang.job.core.handler.IJobHandler;
import cn.aulang.job.core.model.TriggerParam;
import cn.aulang.job.thread.JobThread;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 任务处理执行器
 *
 * @author wulang
 */
public class JobHandlerExecutor implements DisposableBean {

    protected final ExecutorService executorService = Executors.newThreadPerTaskExecutor(
            Thread.ofVirtual().name("JobHandlerExecutor-").factory());

    public Future<?> submit(IJobHandler jobHandler, TriggerParam param, CallbackExecutor callback) {
        return executorService.submit(new JobThread(jobHandler, param, callback));
    }

    @Override
    public void destroy() {
        executorService.shutdownNow();
    }
}
