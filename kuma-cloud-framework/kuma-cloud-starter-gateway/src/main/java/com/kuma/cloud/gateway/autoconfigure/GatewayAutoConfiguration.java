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

package com.kuma.cloud.gateway.autoconfigure;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.cloud.gateway.autoconfigure.properties.GatewayCloudProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * GatewayAutoConfiguration
 *
 * @author kuma
 * @since 2026-04-23
 */
@AutoConfiguration
@EnableConfigurationProperties(GatewayCloudProperties.class)
@ConditionalOnProperty(prefix = GatewayCloudProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class GatewayAutoConfiguration implements InitializingBean {

    private final GatewayCloudProperties properties;

    public GatewayAutoConfiguration(GatewayCloudProperties properties) {
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(GatewayAutoConfiguration.class, StarterNameConstants.GATEWAY_CLOUD_STARTER);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = GatewayCloudProperties.PREFIX + ".cors", name = "enabled", havingValue = "true", matchIfMissing = true)
    public CorsWebFilter corsWebFilter() {
        GatewayCloudProperties.Cors cors = properties.getCors();
        CorsConfiguration config = new CorsConfiguration();
        cors.getAllowedOrigins().forEach(config::addAllowedOrigin);
        cors.getAllowedMethods().forEach(config::addAllowedMethod);
        cors.getAllowedHeaders().forEach(config::addAllowedHeader);
        config.setAllowCredentials(cors.isAllowCredentials());
        config.setMaxAge(cors.getMaxAge());
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}
