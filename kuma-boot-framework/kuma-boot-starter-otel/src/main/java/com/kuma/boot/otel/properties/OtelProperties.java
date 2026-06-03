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

package com.kuma.boot.otel.properties;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OpenTelemetry 链路追踪配置（{@code kuma.boot.otel.*}）.
 *
 * <p>作为统一配置入口，启动阶段由 {@link com.kuma.boot.otel.env.OtelEnvironmentPostProcessor}
 * 中继到 Spring Boot 原生的 {@code management.tracing.*} / {@code management.otlp.tracing.*}，
 * 单一 endpoint 即可对接 Tempo / Jaeger / Zipkin（OTLP 接收端）。
 * 业务应用仍可直接覆盖 {@code management.*} 做精细化控制。
 *
 * @author kuma
 */
@Data
@ConfigurationProperties(prefix = OtelProperties.PREFIX)
public class OtelProperties {

    public static final String PREFIX = "kuma.boot.otel";

    /** 是否启用链路追踪. */
    private boolean enabled = false;

    /** OTLP 导出端点（http/protobuf）. Tempo: 4318/v1/traces；Jaeger / Zipkin OTLP 接收端同理. */
    private String endpoint = "http://localhost:4318/v1/traces";

    /** OTLP 导出请求头，用于鉴权（如 Grafana Cloud 的 Authorization、X-Scope-OrgID）. */
    private Map<String, String> headers = new LinkedHashMap<>();

    /** 导出超时时间（如 10s），为空时使用 Boot 默认值. */
    private String timeout;

    /** 压缩方式：none / gzip，为空时使用 Boot 默认值. */
    private String compression;

    /** 采样配置. */
    private Sampling sampling = new Sampling();

    @Data
    public static class Sampling {
        /** 采样率 0.0~1.0，默认 0.1（10%）. */
        private float probability = 0.1f;
    }
}
