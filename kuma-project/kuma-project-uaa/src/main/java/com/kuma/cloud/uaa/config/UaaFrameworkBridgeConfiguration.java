package com.kuma.cloud.uaa.config;

import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2EndpointProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * 将 UAA 业务配置同步到框架 OAuth2 端点属性，供消费方与文档工具统一引用。
 */
@Configuration
@RequiredArgsConstructor
public class UaaFrameworkBridgeConfiguration {

    private final UaaProperties uaaProperties;
    private final OAuth2EndpointProperties endpointProperties;

    @PostConstruct
    public void syncFrameworkEndpoints() {
        String issuer = uaaProperties.getIssuer();
        endpointProperties.setIssuerUri(issuer);
        endpointProperties.setUaaServiceUri(issuer);
    }
}
