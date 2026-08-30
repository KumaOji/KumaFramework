/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.security.spring.autoconfigure.cloud;

import com.kuma.boot.common.constant.ServiceNameConstants;
import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2EndpointProperties;
import cn.hutool.core.util.StrUtil;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(DiscoveryClient.class)
@ConditionalOnBean(DiscoveryClient.class)
@EnableConfigurationProperties(OAuth2EndpointProperties.class)
public class JwtCloudAutoConfiguration {

    private static final String JWK_SET_PATH = "/oauth2/jwks";

    @Bean
    public JwtUriFactory jwtUriFactory(
            DiscoveryClient discoveryClient, OAuth2EndpointProperties endpointProperties) {
        return () -> resolveJwkSetUri(discoveryClient, endpointProperties);
    }

    private static String resolveJwkSetUri(
            DiscoveryClient discoveryClient, OAuth2EndpointProperties endpointProperties) {
        if (endpointProperties != null && StrUtil.isNotBlank(endpointProperties.getJwkSetUri())) {
            return endpointProperties.getJwkSetUri();
        }

        for (String serviceName : candidateServiceNames(endpointProperties)) {
            String jwkSetUri = discoveryClient.getInstances(serviceName).stream()
                    .findFirst()
                    .map(JwtCloudAutoConfiguration::toJwkSetUri)
                    .orElse(null);
            if (StrUtil.isNotBlank(jwkSetUri)) {
                return jwkSetUri;
            }
        }
        return null;
    }

    private static List<String> candidateServiceNames(OAuth2EndpointProperties endpointProperties) {
        Set<String> names = new LinkedHashSet<>();
        if (endpointProperties != null && StrUtil.isNotBlank(endpointProperties.getUaaServiceName())) {
            names.add(endpointProperties.getUaaServiceName());
        }
        names.add(ServiceNameConstants.KUMA_CLOUD_UAA);
        names.add(ServiceNameConstants.KUMA_CLOUD_AUTH);
        return new ArrayList<>(names);
    }

    private static String toJwkSetUri(ServiceInstance instance) {
        return String.format("http://%s:%s%s", instance.getHost(), instance.getPort(), JWK_SET_PATH);
    }
}
