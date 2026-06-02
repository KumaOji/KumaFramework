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

package com.kuma.cloud.tracing.configuration;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.cloud.tracing.properties.TracingCloudProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 分布式链路追踪标准化自动配置。
 *
 * <p>基于 Micrometer Tracing 门面 + OpenTelemetry SDK 桥接 + OTLP Exporter，作为 SDK 方案与
 * {@code kuma-boot-starter-skywalking}（agent 方案）互补。实际的 Tracer / SpanExporter Bean 由
 * Spring Boot Actuator 的原生自动装配创建，本类负责：
 *
 * <ul>
 *   <li>暴露 {@link TracingCloudProperties} 统一配置入口（{@code kuma.cloud.tracing.*}）；
 *   <li>条件化启用并打印启动标识。
 * </ul>
 *
 * <p>endpoint / 采样率 / headers 等由 {@code TracingEnvironmentPostProcessor} 中继到
 * {@code management.tracing.*} / {@code management.otlp.tracing.*}，单一 endpoint 即可对接
 * Tempo / Jaeger / Zipkin（OTLP 接收端）。
 *
 * @author kuma
 * @since 2026.06
 */
@AutoConfiguration
@EnableConfigurationProperties(TracingCloudProperties.class)
@ConditionalOnClass(name = "io.micrometer.tracing.Tracer")
@ConditionalOnProperty(prefix = TracingCloudProperties.PREFIX, name = "enabled", havingValue = "true")
public class TracingCloudAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(TracingCloudAutoConfiguration.class, StarterNameConstants.TRACING_CLOUD_STARTER);
    }
}
