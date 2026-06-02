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

package com.kuma.cloud.openfeign.properties;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * OpenFeign 声明式调用配置.
 *
 * @author kuma
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = OpenFeignCloudProperties.PREFIX)
public class OpenFeignCloudProperties {

    public static final String PREFIX = "kuma.cloud.openfeign";

    /** 是否启用. */
    private boolean enabled = true;

    /** 连接超时（毫秒）. */
    private long connectTimeout = 5000;

    /** 读超时（毫秒）. */
    private long readTimeout = 10000;

    /** 是否跟随重定向. */
    private boolean followRedirects = true;

    /**
     * Feign 日志级别：NONE / BASIC / HEADERS / FULL.
     *
     * <p>注意：Feign 日志仅在对应 logger 的级别为 DEBUG 时才会输出。
     */
    private FeignLogLevel loggerLevel = FeignLogLevel.NONE;

    /** 请求头透传配置. */
    private Propagation propagation = new Propagation();

    public enum FeignLogLevel {
        NONE,
        BASIC,
        HEADERS,
        FULL
    }

    /**
     * 请求头透传：服务 A 调用服务 B 时，将入站请求中的指定请求头自动带到出站调用，
     * 用于贯穿调用链的鉴权 Token、TraceId、灰度标签等。
     */
    @Data
    public static class Propagation {

        /** 是否启用请求头透传. */
        private boolean enabled = true;

        /** 需要透传的请求头名称（大小写不敏感）. */
        private List<String> headers = new ArrayList<>(List.of(
                "Authorization",
                "X-Request-Id",
                "X-Trace-Id",
                "X-B3-TraceId",
                "X-B3-SpanId",
                "X-Gray-Tag"));
    }
}
