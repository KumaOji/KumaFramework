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

package com.kuma.boot.webflux.autoconfigure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * WebFlux 模块配置属性
 *
 * @author kuma
 */
@Data
@ConfigurationProperties(prefix = WebFluxProperties.PREFIX)
public class WebFluxProperties {

    public static final String PREFIX = "kuma.webflux";

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * CORS 配置
     */
    @NestedConfigurationProperty
    private Cors cors = new Cors();

    /**
     * 链路追踪过滤器配置
     */
    @NestedConfigurationProperty
    private Trace trace = new Trace();

    @Data
    public static class Cors {
        /** 是否启用全局 CORS 配置 */
        private boolean enabled = true;
        /** 允许的路径模式 */
        private String mapping = "/**";
        /** 预检请求缓存时间（秒） */
        private long maxAge = 3600L;
    }

    @Data
    public static class Trace {
        /** 是否启用链路追踪 WebFilter */
        private boolean enabled = true;
    }
}
