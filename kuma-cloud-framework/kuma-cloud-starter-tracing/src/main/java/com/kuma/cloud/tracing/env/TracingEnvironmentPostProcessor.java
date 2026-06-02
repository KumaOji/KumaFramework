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

package com.kuma.cloud.tracing.env;

import com.kuma.cloud.tracing.properties.TracingCloudProperties;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

/**
 * 将 {@code kuma.cloud.tracing.*} 中继到 Spring Boot 原生的 {@code management.tracing.*} /
 * {@code management.otlp.tracing.*}，以驱动 Boot 内置的 Micrometer Tracing + OTLP 自动装配。
 *
 * <p>中继属性源以最低优先级（addLast）注入，因此用户在 application.yml 中显式声明的
 * {@code management.*} 始终优先，可用于精细化覆盖。
 *
 * @author kuma
 * @since 2026.06
 */
public class TracingEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String PROPERTY_SOURCE_NAME = "kumaCloudTracing";

    @Override
    public void postProcessEnvironment(
            ConfigurableEnvironment environment, SpringApplication application) {

        boolean enabled =
                environment.getProperty(TracingCloudProperties.PREFIX + ".enabled", Boolean.class, false);
        if (!enabled) {
            return;
        }

        TracingCloudProperties props =
                Binder.get(environment)
                        .bind(TracingCloudProperties.PREFIX, TracingCloudProperties.class)
                        .orElseGet(TracingCloudProperties::new);

        Map<String, Object> relayed = new LinkedHashMap<>();
        relayed.put("management.tracing.enabled", true);
        relayed.put(
                "management.tracing.sampling.probability", props.getSampling().getProbability());

        if (StringUtils.hasText(props.getEndpoint())) {
            relayed.put("management.otlp.tracing.endpoint", props.getEndpoint());
        }
        if (StringUtils.hasText(props.getTimeout())) {
            relayed.put("management.otlp.tracing.timeout", props.getTimeout());
        }
        if (StringUtils.hasText(props.getCompression())) {
            relayed.put("management.otlp.tracing.compression", props.getCompression());
        }
        if (props.getHeaders() != null) {
            props.getHeaders()
                    .forEach(
                            (key, value) ->
                                    relayed.put("management.otlp.tracing.headers." + key, value));
        }

        if (environment.getPropertySources().contains(PROPERTY_SOURCE_NAME)) {
            return;
        }
        environment
                .getPropertySources()
                .addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, relayed));
    }

    @Override
    public int getOrder() {
        // 在 ConfigDataEnvironmentPostProcessor 之后执行，确保已能读取到 application.yml 中的 kuma.cloud.tracing.*
        return Ordered.LOWEST_PRECEDENCE;
    }
}
