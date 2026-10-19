package com.kuma.cloud.blog.integration.uaa;

import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2EndpointProperties;
import com.kuma.cloud.blog.domain.vo.UaaAuthSettingsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@Slf4j
public class UaaAuthSettingsClient {

    private final RestClient restClient;
    private final OAuth2EndpointProperties endpointProperties;

    public UaaAuthSettingsClient(RestClient.Builder restClientBuilder, OAuth2EndpointProperties endpointProperties) {
        this.restClient = restClientBuilder.build();
        this.endpointProperties = endpointProperties;
    }

    public UaaAuthSettingsVO fetch() {
        String uri = endpointProperties.getUaaServiceUri() + "/api/public/auth-settings";
        try {
            var result = UaaResponse.object(restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(UaaResponse.TYPE));
            if (!(result.get("mfaEnabled") instanceof Boolean enabled)) {
                throw new BusinessException("统一认证中心未返回二次验证状态，请稍后重试");
            }
            UaaAuthSettingsVO vo = new UaaAuthSettingsVO();
            vo.setMfaEnabled(enabled);
            vo.setCaptchaEnabled(Boolean.TRUE.equals(result.get("captchaEnabled")));
            vo.setMfaIssuer(result.get("mfaIssuer") instanceof String issuer ? issuer : null);
            return vo;
        } catch (RestClientException exception) {
            log.warn("拉取 UAA 认证配置失败: {} - {}", uri, exception.getMessage());
            throw new BusinessException("暂时无法获取统一认证中心状态，请稍后重试");
        }
    }

}
