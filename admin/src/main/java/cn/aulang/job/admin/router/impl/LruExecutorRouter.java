package cn.aulang.job.admin.router.impl;

import cn.aulang.job.admin.router.ExecutorRouter;
import cn.aulang.job.core.model.Response;
import cn.aulang.job.core.model.TriggerParam;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 最久未使用路由
 */
public class LruExecutorRouter implements ExecutorRouter {

    private static final Map<Long, LinkedHashMap<String, String>> JOB_LRU_CACHE = new ConcurrentHashMap<>();
    private static long CACHE_VALID_TIME = 0;

    public String route(long jobId, List<String> addressList) {
        // 缓存一天
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            JOB_LRU_CACHE.clear();
            CACHE_VALID_TIME = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
        }

        LinkedHashMap<String, String> lruItem = JOB_LRU_CACHE.computeIfAbsent(jobId, k -> new LinkedHashMap<>(16, 0.75f, true));

        for (String address : addressList) {
            lruItem.putIfAbsent(address, address);
        }

        // 移除不存在的
        Set<String> addressSet = new HashSet<>(addressList);
        lruItem.entrySet().removeIf(entry -> !addressSet.contains(entry.getKey()));

        return lruItem.entrySet().iterator().next().getValue();
    }

    @Override
    public Response<String> route(TriggerParam triggerParam, List<String> addressList) {
        if (addressList.size() == 1) {
            return Response.success(addressList.get(0));
        }

        String address = route(triggerParam.getJobId(), addressList);
        return Response.success(address);
    }
}
