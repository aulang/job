package cn.aulang.job.core.api;


import cn.aulang.job.core.model.CallbackParam;
import cn.aulang.job.core.model.HandlerRegisterParam;
import cn.aulang.job.core.model.RegisterParam;
import cn.aulang.job.core.model.Response;

import java.util.List;

/**
 * 调度端API
 *
 * @author wulang
 */
public interface AdminApi {

    /**
     * 执行器注册
     *
     * @param param       注册参数
     * @param accessToken 认证令牌
     * @return 结果
     */
    Response<String> register(RegisterParam param, String accessToken);

    /**
     * 执行器回调
     *
     * @param param       回调参数
     * @param accessToken 认证令牌
     * @return 结果
     */
    Response<String> callback(CallbackParam param, String accessToken);

    /**
     * 取消注册
     *
     * @param param       取消注册参数
     * @param accessToken 认证令牌
     * @return 结果
     */
    Response<String> unregister(RegisterParam param, String accessToken);


    /**
     * 注册处理器
     *
     * @param param       处理器注册参数
     * @param accessToken 认证令牌
     * @return 结果
     */
    Response<String> registerHandler(HandlerRegisterParam param, String accessToken);

    /**
     * 获取服务端地址
     *
     * @param accessToken 认证令牌
     * @return 服务端地址列表
     */
    Response<List<String>> address(String accessToken);
}
