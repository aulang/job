package cn.aulang.job.executor;

import cn.aulang.job.core.api.AdminApi;
import cn.aulang.job.core.model.CallbackParam;
import cn.aulang.job.core.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 回调执行器
 *
 * @author wulang
 */
@Slf4j
public class CallbackExecutor implements DisposableBean {

    protected final AdminApi adminApi;
    protected final String accessToken;

    protected final LinkedBlockingQueue<CallbackParam> callbackQueue = new LinkedBlockingQueue<>();

    protected final ExecutorService executorService = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name("JobCallbackExecutor-").factory());

    public CallbackExecutor(AdminApi adminApi, String accessToken) {
        this.adminApi = adminApi;
        this.accessToken = accessToken;

        executorService.execute(this::callback);
    }

    public void execute(CallbackParam param) {
        if (!callbackQueue.offer(param)) {
            log.error("Callback queue full, current size: {}, CallbackParam: {}", callbackQueue.size(), param);
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    protected void callback() {
        while (true) {
            try {
                CallbackParam param = callbackQueue.take();

                Response<String> result = adminApi.callback(param, accessToken);
                if (!result.isSuccess()) {
                    log.error("Executor callback fail: {}", result.getMessage());
                }
            } catch (Exception e) {
                log.error("Take callback queue item fail", e);
            }
        }
    }

    @Override
    public void destroy() {
        executorService.shutdownNow();

        if (!callbackQueue.isEmpty()) {
            log.warn("JobCallbackExecutor shutdown, callback queue size: {}", callbackQueue.size());
        }
    }
}
