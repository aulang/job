package cn.aulang.job.client;

import cn.aulang.job.core.api.AdminApi;
import cn.aulang.job.core.model.CallbackParam;
import cn.aulang.job.core.model.HandlerRegisterParam;
import cn.aulang.job.core.model.RegisterParam;
import cn.aulang.job.core.model.Response;
import lombok.Getter;

import java.util.List;

/**
 * 重试调度器接口
 *
 * @author wulang
 */
@Getter
public class RetryAdminClient implements AdminApi {

    protected final int retry;
    protected final AdminClient client;

    public RetryAdminClient(String address) {
        this(address, 3);
    }

    public RetryAdminClient(String address, int retry) {
        this.retry = (retry > 0 ? retry : 3);
        this.client = new AdminClient(address);
    }

    @Override
    public Response<String> register(RegisterParam param, String accessToken) {
        Response<String> result = Response.fail();

        for (int i = 0; i < retry; i++) {
            result = getClient().register(param, accessToken);
            if (result.isSuccess() || result.isFail()) {
                return result;
            }
        }

        return result;
    }

    @Override
    public Response<String> callback(CallbackParam param, String accessToken) {
        Response<String> result = Response.fail();

        for (int i = 0; i < retry; i++) {
            result = getClient().callback(param, accessToken);

            if (result.isSuccess() || result.isFail()) {
                return result;
            }
        }

        return result;
    }

    @Override
    public Response<String> unregister(RegisterParam param, String accessToken) {
        Response<String> result = Response.fail();

        for (int i = 1; i < retry; i++) {
            result = getClient().unregister(param, accessToken);

            if (result.isSuccess() || result.isFail()) {
                return result;
            }
        }

        return result;
    }

    @Override
    public Response<String> registerHandler(HandlerRegisterParam param, String accessToken) {
        Response<String> result = Response.fail();

        for (int i = 0; i < retry; i++) {
            result = getClient().registerHandler(param, accessToken);

            if (result.isSuccess() || result.isFail()) {
                return result;
            }
        }

        return result;
    }

    @Override
    public Response<List<String>> address(String accessToken) {
        Response<List<String>> result = Response.fail();

        for (int i = 0; i < retry; i++) {
            result = getClient().address(accessToken);

            if (result.isSuccess() || result.isFail()) {
                return result;
            }
        }

        return result;
    }
}
