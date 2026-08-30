package com.kuma.cloud.blog.integration.uaa;

import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2EndpointProperties;
import com.kuma.cloud.blog.domain.vo.UaaAuthSettingsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
@Slf4j
public class UaaAuthSettingsClient {

    private static final ParameterizedTypeReference<Result<UaaAuthSettingsVO>> RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {};

    private final RestClient restClient = RestClient.create();
    private final OAuth2EndpointProperties endpointProperties;

    public UaaAuthSettingsVO fetch() {
        String uri = endpointProperties.getUaaServiceUri() + "/api/public/auth-settings";
        try {
            Result<UaaAuthSettingsVO> result = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(RESPONSE_TYPE);
            if (result == null || result.getData() == null) {
                log.warn("UAA 认证配置响应为空: {}", uri);
                return disabled();
            }
            return result.getData();
        } catch (RestClientException exception) {
            log.warn("拉取 UAA 认证配置失败: {} - {}", uri, exception.getMessage());
            return disabled();
        }
    }

    private UaaAuthSettingsVO disabled() {
        UaaAuthSettingsVO vo = new UaaAuthSettingsVO();
        vo.setMfaEnabled(false);
        return vo;
    }
}
