package cn.aulang.job.core.api;


import cn.aulang.job.core.model.IdleBeatParam;
import cn.aulang.job.core.model.KillParam;
import cn.aulang.job.core.model.LogParam;
import cn.aulang.job.core.model.LogResult;
import cn.aulang.job.core.model.Response;
import cn.aulang.job.core.model.TriggerParam;

/**
 * 执行器端API
 *
 * @author wulang
 */
public interface ExecutorApi {

    /**
     * 执行器心跳
     *
     * @param accessToken 令牌
     * @return 响应
     */
    Response<String> beat(String accessToken);

    /**
     * 正在运行任务数
     *
     * @param accessToken 令牌
     * @return 响应
     */
    Response<Integer> running(String accessToken);

    /**
     * 执行器Job空闲
     *
     * @param param       参数
     * @param accessToken 令牌
     * @return 响应
     */
    Response<String> idleBeat(IdleBeatParam param, String accessToken);

    /**
     * 任务执行
     *
     * @param param       参数
     * @param accessToken 访问令牌
     * @return 结果
     */
    Response<String> run(TriggerParam param, String accessToken);

    /**
     * 取消任务
     *
     * @param param       参数
     * @param accessToken 令牌
     * @return 结果
     */
    Response<String> kill(KillParam param, String accessToken);

    /**
     * 读取日志
     *
     * @param param       参数
     * @param accessToken 令牌
     * @return 日志
     */
    Response<LogResult> log(LogParam param, String accessToken);
}
