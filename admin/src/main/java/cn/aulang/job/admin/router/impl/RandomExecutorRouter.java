package cn.aulang.job.admin.router.impl;

import cn.aulang.job.admin.router.ExecutorRouter;
import cn.aulang.job.core.model.Response;
import cn.aulang.job.core.model.TriggerParam;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机路由
 */
public class RandomExecutorRouter implements ExecutorRouter {

    @Override
    public Response<String> route(TriggerParam triggerParam, List<String> addressList) {
        if (addressList.size() == 1) {
            return Response.success(addressList.getFirst());
        }

        String address = addressList.get(ThreadLocalRandom.current().nextInt(addressList.size()));
        return Response.success(address);
    }
}
