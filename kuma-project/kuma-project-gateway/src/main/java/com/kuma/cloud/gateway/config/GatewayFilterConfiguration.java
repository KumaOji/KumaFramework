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

package com.kuma.cloud.gateway.config;

import com.kuma.cloud.gateway.filter.ClientAccessGlobalFilter;
import com.kuma.cloud.gateway.filter.GatewayTraceGlobalFilter;
import com.kuma.cloud.gateway.filter.IdentityRelayGlobalFilter;
import com.kuma.cloud.gateway.filter.RequestLogGlobalFilter;
import com.kuma.cloud.gateway.filter.StripIdentityHeadersGlobalFilter;
import com.kuma.cloud.gateway.properties.GatewayCloudProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关全局过滤器注册。
 *
 * @author kuma
 * @since 2026-04-23
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = GatewayCloudProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class GatewayFilterConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public StripIdentityHeadersGlobalFilter stripIdentityHeadersGlobalFilter() {
        return new StripIdentityHeadersGlobalFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public GatewayTraceGlobalFilter gatewayTraceGlobalFilter() {
        return new GatewayTraceGlobalFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = GatewayCloudProperties.PREFIX + ".access", name = "enabled", havingValue = "true")
    public ClientAccessGlobalFilter clientAccessGlobalFilter(GatewayCloudProperties properties) {
        return new ClientAccessGlobalFilter(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public IdentityRelayGlobalFilter identityRelayGlobalFilter(GatewayCloudProperties properties) {
        return new IdentityRelayGlobalFilter(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = GatewayCloudProperties.PREFIX + ".log", name = "enabled", havingValue = "true", matchIfMissing = true)
    public RequestLogGlobalFilter requestLogGlobalFilter(
            GatewayCloudProperties properties, ObjectProvider<com.kuma.boot.ip2region.model.Ip2regionSearcher> ip2regionSearcher) {
        return new RequestLogGlobalFilter(properties, ip2regionSearcher);
    }
}
