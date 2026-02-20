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

package com.kuma.boot.spring.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Web 配置属性
 *
 * @author kuma
 */
@Data
@ConfigurationProperties(prefix = WebProperties.PREFIX)
public class WebProperties {

    public static final String PREFIX = "ballcat.web";

    /**
     * traceId 的 http 头名称
     */
    private String traceIdHeaderName = "X-Trace-Id";

    /**
     * 跨域配置
     */
    private CorsConfig corsConfig;

    @Data
    public static class CorsConfig {

        private boolean enabled = false;
        private String urlPattern = "/**";
        private List<String> allowedOrigins;
        private List<String> allowedOriginPatterns;
        private List<String> allowedMethods = new ArrayList<>(Collections.singletonList(CorsConfiguration.ALL));
        private List<String> allowedHeaders = new ArrayList<>(Collections.singletonList(CorsConfiguration.ALL));
        private List<String> exposedHeaders = new ArrayList<>(Collections.singletonList("traceId"));
        private Boolean allowCredentials = true;
        private Long maxAge;
    }
}
