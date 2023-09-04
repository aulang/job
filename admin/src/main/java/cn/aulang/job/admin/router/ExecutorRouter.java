package cn.aulang.job.admin.router;

import cn.aulang.job.core.model.Response;
import cn.aulang.job.core.model.TriggerParam;

import java.util.List;

/**
 * 执行器路由器
 *
 * @author wulang
 */
public interface ExecutorRouter {

    /**
     * 执行器路由选择
     *
     * @param triggerParam 任务触发参数
     * @param addresses    执行器地址
     * @return 结果
     */
    Response<String> route(TriggerParam triggerParam, List<String> addresses);
}
