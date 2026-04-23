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

import com.kuma.cloud.gateway.autoconfigure.properties.GatewayCloudProperties;
import com.kuma.cloud.gateway.filter.AuthGlobalFilter;
import com.kuma.cloud.gateway.filter.RequestLogGlobalFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * GatewayFilterAutoConfiguration
 *
 * @author kuma
 * @since 2026-04-23
 */
@AutoConfiguration(after = GatewayAutoConfiguration.class)
public class GatewayFilterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = GatewayCloudProperties.PREFIX + ".log", name = "enabled", havingValue = "true", matchIfMissing = true)
    public RequestLogGlobalFilter requestLogGlobalFilter() {
        return new RequestLogGlobalFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = GatewayCloudProperties.PREFIX + ".auth", name = "enabled", havingValue = "true")
    public AuthGlobalFilter authGlobalFilter(GatewayCloudProperties properties) {
        return new AuthGlobalFilter(properties);
    }
}
