package cn.aulang.job.client;

import cn.aulang.job.config.JobProperties;
import cn.aulang.job.core.model.RegisterParam;
import cn.aulang.job.core.model.Response;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.LongAdder;

/**
 * 负载均衡调度器接口
 *
 * @author wulang
 */
public class LoadBalancerAdminClient extends RetryAdminClient {

    private final JobProperties properties;
    private final LongAdder count = new LongAdder();
    private final List<String> addresses = new CopyOnWriteArrayList<>();

    public LoadBalancerAdminClient(JobProperties properties) {
        super(properties.getAdminUrl(), properties.getRetry());

        this.properties = properties;

        Response<List<String>> result = client.address(properties.getAccessToken());
        if (result.isSuccess()) {
            List<String> addresses = result.getData();
            if (CollectionUtils.isEmpty(addresses)) {
                this.addresses.add(properties.getAdminUrl());
            } else {
                this.addresses.addAll(addresses);
            }
        } else {
            throw new RuntimeException("No admin server available");
        }
    }

    /**
     * 轮询负载均衡
     *
     * @return 调度服务端地址
     */
    private String choose() {
        int size = addresses.size();
        if (size > 1) {
            count.increment();
            int value = count.intValue();
            if (value > 65535) {
                count.reset();
            }
            return addresses.get(value % size);
        } else {
            return addresses.getFirst();
        }
    }

    @Override
    public AdminClient getClient() {
        client.setAddress(choose());
        return client;
    }

    @Override
    public Response<String> register(RegisterParam param, String accessToken) {
        Response<String> result = Response.fail();

        for (int i = 0; i < retry; i++) {
            result = getClient().register(param, accessToken);
            if (result.isSuccess() || result.isFail()) {
                return result;
            } else {
                // 网络错误
                refreshAddress();
            }
        }

        return result;
    }

    private void refreshAddress() {
        Response<List<String>> result = getClient().address(properties.getAccessToken());
        if (result.isSuccess()) {
            List<String> addresses = result.getData();
            if (!CollectionUtils.isEmpty(addresses)) {
                this.addresses.addAll(addresses);
            }
        }
    }
}
