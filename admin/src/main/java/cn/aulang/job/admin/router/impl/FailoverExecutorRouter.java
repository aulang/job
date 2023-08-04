package cn.aulang.job.admin.router.impl;


import cn.aulang.job.admin.client.ExecutorClient;
import cn.aulang.job.admin.router.ExecutorRouter;
import cn.aulang.job.core.model.Response;
import cn.aulang.job.core.model.TriggerParam;

import java.util.List;

/**
 * 快速失败路由
 */
public class FailoverExecutorRouter implements ExecutorRouter {

    private final String accessToken;

    public FailoverExecutorRouter(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public Response<String> route(TriggerParam triggerParam, List<String> addressList) {
        if (addressList.size() == 1) {
            return Response.success(addressList.get(0));
        }

        ExecutorClient executorClient = new ExecutorClient();
        for (String address : addressList) {
            executorClient.setAddress(address);
            Response<String> result = executorClient.beat(accessToken);
            if (result.isSuccess()) {
                return Response.success(address);
            }
        }

        return Response.fail("No executor available");
    }
}
