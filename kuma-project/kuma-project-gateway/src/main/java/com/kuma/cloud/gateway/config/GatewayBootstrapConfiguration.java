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

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.cloud.gateway.properties.GatewayCloudProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 网关模块引导配置：启用框架属性绑定并输出 KMC 启动日志。
 *
 * @author kuma
 * @since 2026-04-23
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(GatewayCloudProperties.class)
@ConditionalOnProperty(prefix = GatewayCloudProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class GatewayBootstrapConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(GatewayBootstrapConfiguration.class, "kuma-project-gateway");
    }
}
