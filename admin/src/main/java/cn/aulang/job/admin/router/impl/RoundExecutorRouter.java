package cn.aulang.job.admin.router.impl;

import cn.aulang.job.admin.router.ExecutorRouter;
import cn.aulang.job.core.model.Response;
import cn.aulang.job.core.model.TriggerParam;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询路由
 */
public class RoundExecutorRouter implements ExecutorRouter {

    private static final Map<Long, AtomicInteger> ROUTE_COUNT_CACHE = new ConcurrentHashMap<>();
    private static long CACHE_VALID_TIME = 0;

    private static int count(long jobId) {
        // 缓存一天
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            ROUTE_COUNT_CACHE.clear();
            CACHE_VALID_TIME = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
        }

        AtomicInteger count = ROUTE_COUNT_CACHE.get(jobId);
        if (count == null || count.get() > 65535) {
            // 初始化时主动Random一次，缓解首次压力
            count = new AtomicInteger(ThreadLocalRandom.current().nextInt(128));
        } else {
            // count++
            count.addAndGet(1);
        }

        ROUTE_COUNT_CACHE.put(jobId, count);

        return count.get();
    }

    @Override
    public Response<String> route(TriggerParam triggerParam, List<String> addressList) {
        if (addressList.size() == 1) {
            return Response.success(addressList.get(0));
        }

        String address = addressList.get(count(triggerParam.getJobId()) % addressList.size());
        return Response.success(address);
    }
}
