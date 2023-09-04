package cn.aulang.job.client;

import cn.aulang.job.core.api.AdminApi;
import cn.aulang.job.core.common.Constants;
import cn.aulang.job.core.model.CallbackParam;
import cn.aulang.job.core.model.HandlerRegisterParam;
import cn.aulang.job.core.model.RegisterParam;
import cn.aulang.job.core.model.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 调度器接口
 *
 * @author wulang
 */
@Slf4j
@Getter
@Setter
public class AdminClient implements AdminApi {

    private String address;
    private RestTemplate restTemplate;

    public AdminClient(String address) {
        this(address, new RestTemplate());
    }

    public AdminClient(String address, RestTemplate restTemplate) {
        this.address = address;
        this.restTemplate = restTemplate;
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
    public Response<String> register(RegisterParam param, String accessToken) {
        String url = getUrl("admin/register");
        HttpEntity<?> entity = getHttpEntity(param, accessToken);
        try {
            ParameterizedTypeReference<Response<String>> reference = new ParameterizedTypeReference<>() {
            };

            return restTemplate.exchange(url, HttpMethod.POST, entity, reference).getBody();
        } catch (ResourceAccessException e) {
            // 网络不通，节点离线
            log.error("Call job admin register API failed", e);
            return Response.netError(e.getMessage());
        } catch (Exception e) {
            log.error("Call job admin register API failed", e);
            return Response.fail(e.getMessage());
        }
    }

    @Override
    public Response<String> callback(CallbackParam param, String accessToken) {
        String url = getUrl("admin/callback");
        HttpEntity<?> entity = getHttpEntity(param, accessToken);
        try {
            ParameterizedTypeReference<Response<String>> reference = new ParameterizedTypeReference<>() {
            };

            return restTemplate.exchange(url, HttpMethod.POST, entity, reference).getBody();
        } catch (ResourceAccessException e) {
            // 网络不通，节点离线
            log.error("Call job admin callback API failed", e);
            return Response.netError(e.getMessage());
        } catch (Exception e) {
            log.error("Call job admin callback API failed", e);
            return Response.fail(e.getMessage());
        }
    }

    @Override
    public Response<String> unregister(RegisterParam param, String accessToken) {
        String url = getUrl("admin/unregister");
        HttpEntity<?> entity = getHttpEntity(param, accessToken);
        try {
            ParameterizedTypeReference<Response<String>> reference = new ParameterizedTypeReference<>() {
            };

            return restTemplate.exchange(url, HttpMethod.POST, entity, reference).getBody();
        } catch (ResourceAccessException e) {
            // 网络不通，节点离线
            log.error("Call job admin unregister API failed", e);
            return Response.netError(e.getMessage());
        } catch (Exception e) {
            log.error("Call job admin unregister API failed", e);
            return Response.fail(e.getMessage());
        }
    }

    @Override
    public Response<String> registerHandler(HandlerRegisterParam param, String accessToken) {
        String url = getUrl("admin/handler");
        HttpEntity<?> entity = getHttpEntity(param, accessToken);
        try {
            ParameterizedTypeReference<Response<String>> reference = new ParameterizedTypeReference<>() {
            };

            return restTemplate.exchange(url, HttpMethod.POST, entity, reference).getBody();
        } catch (ResourceAccessException e) {
            // 网络不通，节点离线
            log.error("Call executor register handler API failed", e);
            return Response.netError(e.getMessage());
        } catch (Exception e) {
            log.error("Call executor register handler API failed", e);
            return Response.fail(e.getMessage());
        }
    }

    @Override
    public Response<List<String>> address(String accessToken) {
        String url = getUrl("admin/address");
        HttpEntity<?> entity = getHttpEntity(null, accessToken);
        try {
            ParameterizedTypeReference<Response<List<String>>> reference = new ParameterizedTypeReference<>() {
            };

            return restTemplate.exchange(url, HttpMethod.POST, entity, reference).getBody();
        } catch (ResourceAccessException e) {
            // 网络不通，节点离线
            log.error("Call job admin address API failed", e);
            return Response.netError(e.getMessage());
        } catch (Exception e) {
            log.error("Call job admin address API failed", e);
            return Response.fail(e.getMessage());
        }
    }
}
