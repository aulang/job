package cn.aulang.job.admin.client;

import cn.aulang.job.core.api.ExecutorApi;
import cn.aulang.job.core.common.Constants;
import cn.aulang.job.core.model.IdleBeatParam;
import cn.aulang.job.core.model.KillParam;
import cn.aulang.job.core.model.LogParam;
import cn.aulang.job.core.model.LogResult;
import cn.aulang.job.core.model.Response;
import cn.aulang.job.core.model.TriggerParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * 执行器接口
 *
 * @author wulang
 */
public class ExecutorClient implements ExecutorApi {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorClient.class);
    private static final RestTemplate restTemplate = new RestTemplate();

    private String address;

    public ExecutorClient() {
    }

    public ExecutorClient(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String getUrl(String suffix) {
        if (address.charAt(address.length() - 1) == '/') {
            return address.concat(suffix);
        } else {
            return address.concat(Constants.SPLIT_DIVIDE).concat(suffix);
        }
    }

    private <T> HttpEntity<T> getHttpEntity(T body, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (StringUtils.isNotBlank(accessToken)) {
            headers.add(Constants.ACCESS_TOKEN_HEADER, accessToken);
        }
        return new HttpEntity<>(body, headers);
    }

    @Override
    public Response<String> beat(String accessToken) {
        String url = getUrl("job-executor/beat");
        HttpEntity<?> entity = getHttpEntity(null, accessToken);
        try {
            ParameterizedTypeReference<Response<String>> reference = new ParameterizedTypeReference<>() {
            };

            return restTemplate.exchange(url, HttpMethod.POST, entity, reference).getBody();
        } catch (ResourceAccessException e) {
            // 网络不通，节点离线
            logger.error("Call executor beat api fail", e);
            return Response.netError(e.getMessage());
        } catch (Exception e) {
            logger.error("Call executor beat api fail", e);
            return Response.fail(e.getMessage());
        }
    }

    @Override
    public Response<Integer> running(String accessToken) {
        String url = getUrl("job-executor/running");
        HttpEntity<?> entity = getHttpEntity(null, accessToken);
        try {
            ParameterizedTypeReference<Response<Integer>> reference = new ParameterizedTypeReference<>() {
            };

            return restTemplate.exchange(url, HttpMethod.POST, entity, reference).getBody();
        } catch (ResourceAccessException e) {
            // 网络不通，节点离线
            logger.error("Call executor beat api fail", e);
            return Response.netError(e.getMessage());
        } catch (Exception e) {
            logger.error("Call executor beat api fail", e);
            return Response.fail(e.getMessage());
        }
    }

    @Override
    public Response<String> idleBeat(IdleBeatParam param, String accessToken) {
        String url = getUrl("job-executor/idle-beat");
        HttpEntity<?> entity = getHttpEntity(param, accessToken);
        try {
            ParameterizedTypeReference<Response<String>> reference = new ParameterizedTypeReference<>() {
            };

            return restTemplate.exchange(url, HttpMethod.POST, entity, reference).getBody();
        } catch (ResourceAccessException e) {
            // 网络不通，节点离线
            logger.error("Call executor idle-beat api fail", e);
            return Response.netError(e.getMessage());
        } catch (Exception e) {
            logger.error("Call executor idle-beat api fail", e);
            return Response.fail(e.getMessage());
        }
    }

    @Override
    public Response<String> run(TriggerParam param, String accessToken) {
        String url = getUrl("job-executor/run");
        HttpEntity<?> entity = getHttpEntity(param, accessToken);
        try {
            ParameterizedTypeReference<Response<String>> reference = new ParameterizedTypeReference<>() {
            };

            return restTemplate.exchange(url, HttpMethod.POST, entity, reference).getBody();
        } catch (ResourceAccessException e) {
            // 网络不通，节点离线
            logger.error("Call executor run api fail", e);
            return Response.netError(e.getMessage());
        } catch (Exception e) {
            logger.error("Call executor run api fail", e);
            return Response.fail(e.getMessage());
        }
    }

    @Override
    public Response<String> kill(KillParam param, String accessToken) {
        String url = getUrl("job-executor/kill");
        HttpEntity<?> entity = getHttpEntity(param, accessToken);
        try {
            ParameterizedTypeReference<Response<String>> reference = new ParameterizedTypeReference<>() {
            };

            return restTemplate.exchange(url, HttpMethod.POST, entity, reference).getBody();
        } catch (ResourceAccessException e) {
            // 网络不通，节点离线
            logger.error("Call executor kill api fail", e);
            return Response.netError(e.getMessage());
        } catch (Exception e) {
            logger.error("Call executor kill api fail", e);
            return Response.fail(e.getMessage());
        }
    }

    @Override
    public Response<LogResult> log(LogParam param, String accessToken) {
        String url = getUrl("job-executor/log");
        HttpEntity<?> entity = getHttpEntity(param, accessToken);
        try {
            ParameterizedTypeReference<Response<LogResult>> reference = new ParameterizedTypeReference<>() {
            };

            return restTemplate.exchange(url, HttpMethod.POST, entity, reference).getBody();
        } catch (ResourceAccessException e) {
            // 网络不通，节点离线
            logger.error("Call executor log api fail", e);
            return Response.netError(e.getMessage());
        } catch (Exception e) {
            logger.error("Call executor log api fail", e);
            return Response.fail(e.getMessage());
        }
    }
}
