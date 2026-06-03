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

package com.kuma.cloud.dubbo.configuration;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.cloud.dubbo.filter.DubboPropagationKeys;
import com.kuma.cloud.dubbo.properties.DubboCloudProperties;
import org.apache.dubbo.rpc.Filter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Dubbo RPC 自动配置.
 *
 * <p>负责将 {@link DubboCloudProperties} 中的透传配置桥接到 Dubbo SPI 过滤器
 * {@code DubboContextPropagationFilter}（由 {@link DubboPropagationKeys} 作中间媒介），
 * 并在启动日志中打印 starter 已就绪的信息。
 *
 * <p>Dubbo 本身的协议、注册中心、超时、重试等仍通过原生 {@code dubbo.*} 配置设置；
 * 业务应用需在启动类上标注 {@code @EnableDubbo} 以扫描
 * {@code @DubboService} / {@code @DubboReference}。
 *
 * @author kuma
 */
@AutoConfiguration
@EnableConfigurationProperties(DubboCloudProperties.class)
@ConditionalOnClass(Filter.class)
@ConditionalOnProperty(
        prefix = DubboCloudProperties.PREFIX,
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class DubboAutoConfiguration implements InitializingBean {

    private final DubboCloudProperties properties;

    public DubboAutoConfiguration(DubboCloudProperties properties) {
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() {
        DubboCloudProperties.Propagation propagation = properties.getPropagation();
        DubboPropagationKeys.configure(propagation.isEnabled(), propagation.getKeys());
        LogUtils.started(DubboAutoConfiguration.class, StarterNameConstants.DUBBO_CLOUD_STARTER);
    }
}
