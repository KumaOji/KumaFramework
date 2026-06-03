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

package com.kuma.boot.security.satoken.properties;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * Sa-Token 增强配置.
 *
 * <p>此处仅维护 kuma 侧的扩展配置（开关、路由白名单）；
 * Sa-Token 自身的 token 名称、超时时间、存储方式等仍通过原生
 * {@code sa-token.*} 配置项设置。
 *
 * @author kuma
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = SaTokenProperties.PREFIX)
public class SaTokenProperties {

    public static final String PREFIX = "kuma.boot.security.sa-token";

    /** 是否启用 Sa-Token 鉴权增强. */
    private boolean enabled = true;

    /**
     * 路由白名单：匹配的请求路径跳过登录校验（Ant 风格路径）.
     *
     * <p>默认放行 Actuator 端点、API 文档、SpringDoc、Knife4j 等运维/文档类路径；
     * 业务应用按需追加，例如 {@code /api/login}、{@code /api/register}。
     */
    private List<String> excludeUrls = new ArrayList<>(List.of(
            "/actuator/**",
            "/doc.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/favicon.ico",
            "/error"));
}
