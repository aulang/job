package cn.aulang.job.admin.router.impl;

import cn.aulang.job.admin.router.ExecutorRouter;
import cn.aulang.job.core.model.Response;
import cn.aulang.job.core.model.TriggerParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 使用频率最低路由
 */
public class LfuExecutorRouter implements ExecutorRouter {

    private static final Map<Long, HashMap<String, Integer>> JOB_LFU_CACHE = new ConcurrentHashMap<>();
    private static long CACHE_VALID_TIME = 0;

    public String route(long jobId, List<String> addressList) {
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            JOB_LFU_CACHE.clear();
            CACHE_VALID_TIME = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
        }

        HashMap<String, Integer> lfuItem = JOB_LFU_CACHE.computeIfAbsent(jobId, k -> new HashMap<>());

        for (String address : addressList) {
            if (!lfuItem.containsKey(address) || lfuItem.get(address) > 65535) {
                // 初始化时主动Random一次，缓解首次压力
                lfuItem.put(address, ThreadLocalRandom.current().nextInt(addressList.size()));
            }
        }

        // 移除不存在的
        Set<String> addressSet = new HashSet<>(addressList);
        lfuItem.entrySet().removeIf(entry -> !addressSet.contains(entry.getKey()));

        List<Map.Entry<String, Integer>> lfuItemList = new ArrayList<>(lfuItem.entrySet());
        lfuItemList.sort(Map.Entry.comparingByValue());

        Map.Entry<String, Integer> entry = lfuItemList.get(0);
        entry.setValue(entry.getValue() + 1);

        return entry.getKey();
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
