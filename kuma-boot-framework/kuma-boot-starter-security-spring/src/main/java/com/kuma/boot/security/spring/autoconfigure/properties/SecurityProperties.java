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

package com.kuma.boot.security.spring.autoconfigure.properties;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 楠岃瘉鏉冮檺閰嶇疆
 */
@ConfigurationProperties(prefix = SecurityProperties.PREFIX)
public class SecurityProperties {

    public static final String PREFIX = "kuma.boot.security";

    public static final String[] ENDPOINTS = {
        "/actuator/**",
        "/v3/**",
        "/*/v3/**",
        "/fallback",
        "/favicon.ico",
        "/swagger-resources/**",
        "/webjars/**",
        "/druid/**",
        "/login/**",
        "/*/*.html",
        "/*/*.css",
        "/*/*.js",
        "/*.js",
        "/*.css",
        "/*.html",
        "/*/favicon.ico",
        "/*/api-docs",
        "/health/**",
        "/css/**",
        "/js/**",
        "/k8s/**",
        "/k8s",
        "/images/**",
        "/doc/**",
        "/swagger-ui.html",
        "/startup-report",
        "/favicon.ico",
        "/actuator/**",
        "/index",
        "/index.html",
        "/doc.html",
        "/request/gateway/test",
        "/error",
    };

    /**
     * 蹇界暐URL锛孡ist鍒楄〃褰㈠紡
     */
    private List<String> ignoreUrl = new ArrayList<>();

    /**
     * 棣栨鍔犺浇鍚堝苟ENDPOINTS
     */
    @PostConstruct
    public void initIgnoreUrl() {
        Collections.addAll(ignoreUrl, ENDPOINTS);
    }

    public List<String> getIgnoreUrl() {
        return ignoreUrl;
    }

    public void setIgnoreUrl(List<String> ignoreUrl) {
        this.ignoreUrl = ignoreUrl;
    }
}
