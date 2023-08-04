package cn.aulang.job.admin.router.impl;

import cn.aulang.job.admin.client.ExecutorClient;
import cn.aulang.job.admin.router.ExecutorRouter;
import cn.aulang.job.core.model.Response;
import cn.aulang.job.core.model.TriggerParam;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 空闲路由
 */
public class BusyOverExecutorRouter implements ExecutorRouter {

    private final String accessToken;

    public BusyOverExecutorRouter(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public Response<String> route(TriggerParam triggerParam, List<String> addressList) {
        if (addressList.size() == 1) {
            return Response.success(addressList.get(0));
        }

        ExecutorClient executorClient = new ExecutorClient();

        TreeMap<Integer, List<String>> treeMap = new TreeMap<>(Integer::compareTo);
        for (String address : addressList) {
            executorClient.setAddress(address);

            Response<Integer> result = executorClient.running(accessToken);
            if (result.isSuccess()) {
                treeMap.computeIfAbsent(result.getData(), k -> new ArrayList<>()).add(address);
            }
        }

        if (treeMap.isEmpty()) {
            return Response.fail("No executor idle");
        }

        List<String> addresses = treeMap.firstEntry().getValue();
        if (addresses.size() == 1) {
            String address = addresses.get(0);
            return Response.success(address);
        } else {
            // 随机选一个
            int index = ThreadLocalRandom.current().nextInt(addresses.size());
            return Response.success(addresses.get(index));
        }
    }
}