/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.cloud.client.discovery.DiscoveryClient
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.kuma.boot.security.spring.configuration.cloud;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods=false)
@ConditionalOnClass(value={DiscoveryClient.class})
@ConditionalOnBean(value={DiscoveryClient.class})
public class JwtCloudAutoConfiguration {
    @Bean
    public JwtUriFactory jwtUriFactory(final DiscoveryClient discoveryClient) {
        return new JwtUriFactory(){

            @Override
            public String jwkSetUri() {
                return discoveryClient.getServices().stream().filter(s -> s.contains("kuma-cloud-auth")).flatMap(s -> discoveryClient.getInstances(s).stream()).map(instance -> String.format("http://%s:%s/oauth2/jwks", instance.getHost(), instance.getPort())).findFirst().orElse(null);
            }
        };
    }
}

