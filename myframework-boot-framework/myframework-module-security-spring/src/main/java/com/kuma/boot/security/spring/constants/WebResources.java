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

package com.kuma.boot.security.spring.constants;

import com.google.common.collect.Lists;
import java.util.List;

/**
 * <p>Web配置常量
 */
public class WebResources {

    public static final List<String> DEFAULT_IGNORED_STATIC_RESOURCES =
            Lists.newArrayList(
                    "/error/**",
                    "/plugins/**",
                    "/kmc/**",
                    "/static/**",
                    "/webjars/**",
                    "/assets/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/swagger-resources/**",
                    "/v3/api-docs",
                    "/v3/api-docs/**",
                    "/openapi.json",
                    "/swagger-ui.html",
                    "/v3/**",
                    "/favicon.ico",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/actuator/**",
                    "/index",
                    "/index.html",
                    "/doc.html",
                    "/*.js",
                    "/*.css",
                    "/*.json",
                    "/*.min.js",
                    "/*.min.css",
                    "/**.js",
                    "/**.css",
                    "/**.json",
                    "/**.min.js",
                    "/**.min.css",
                    "/component/**",
                    "/login/**",
                    "/actuator/**",
                    "/h2-console/**",
                    "/pear.config.json",
                    "/pear.config.yml",
                    "/admin/css/**",
                    "/admin/fonts/**",
                    "/admin/js/**",
                    "/admin/images/**",
                    "/health/**",
                    "/favicon.ico",
                    "/swagger-ui.html",
                    "/v3/**",
                    "/favicon.ico",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/actuator/**",
                    "/index",
                    "/index.html",
                    "/doc.html",
                    "/*.js",
                    "/*.css",
                    "/*.json",
                    "/*.min.js",
                    "/*.min.css",
                    "/**.js",
                    "/**.css",
                    "/**.json",
                    "/**.min.js",
                    "/**.min.css",
                    "/component/**",
                    "/login/**",
                    "/actuator/**",
                    "/h2-console/**",
                    "/pear.config.json",
                    "/pear.config.yml",
                    "/admin/css/**",
                    "/admin/fonts/**",
                    "/admin/js/**",
                    "/admin/images/**",
                    "/health/**");

    public static final List<String> DEFAULT_PERMIT_ALL_RESOURCES =
            Lists.newArrayList("/open/**", "/stomp/ws", "/oauth2/sign-out", "/login*");

    public static final List<String> DEFAULT_HAS_AUTHENTICATED_RESOURCES =
            Lists.newArrayList("/engine-rest/**");
}
