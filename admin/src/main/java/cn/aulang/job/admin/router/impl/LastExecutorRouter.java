package cn.aulang.job.admin.router.impl;


import cn.aulang.job.admin.router.ExecutorRouter;
import cn.aulang.job.core.model.Response;
import cn.aulang.job.core.model.TriggerParam;

import java.util.List;

/**
 * 最后一个路由
 */
public class LastExecutorRouter implements ExecutorRouter {

    @Override
    public Response<String> route(TriggerParam triggerParam, List<String> addressList) {
        return Response.success(addressList.getLast());
    }
}
