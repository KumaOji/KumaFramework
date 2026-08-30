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

package com.kuma.cloud.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 网关配置项，前缀 {@code kuma.cloud.gateway}。
 *
 * <p>路由本身由 Spring Cloud Gateway 的 {@code spring.cloud.gateway.server.webflux.routes} 描述，
 * 这里只承载网关的横切能力：CORS、鉴权、IP 访问控制、访问日志与限流键策略。
 *
 * @author kuma
 * @since 2026-04-23
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = GatewayCloudProperties.PREFIX)
public class GatewayCloudProperties {

    public static final String PREFIX = "kuma.cloud.gateway";

    /**
     * 网关横切能力总开关，关闭后 CORS / 鉴权 / 日志 / IP 控制全部不装配，仅保留纯转发。
     */
    private boolean enabled = true;

    /**
     * 是否信任上游代理写入的 {@code X-Forwarded-For}。仅当网关部署在受控的 Nginx / SLB 之后才置为 true，
     * 否则客户端可以伪造该头绕过 IP 黑名单与按 IP 限流。
     */
    private boolean trustProxyHeaders = false;

    private Cors cors = new Cors();
    private Auth auth = new Auth();
    private Access access = new Access();
    private Log log = new Log();
    private RateLimit rateLimit = new RateLimit();

    /**
     * 跨域配置。网关是浏览器唯一入口，跨域在此统一收口，下游服务不必再各自开放 CORS。
     */
    @Data
    public static class Cors {

        private boolean enabled = true;

        /**
         * 使用 origin pattern 而非 origin，允许 {@code allowCredentials=true} 时仍支持通配。
         */
        private List<String> allowedOriginPatterns = new ArrayList<>(List.of("*"));

        private List<String> allowedMethods =
                new ArrayList<>(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));

        private List<String> allowedHeaders = new ArrayList<>(List.of("*"));

        /**
         * 需要暴露给浏览器脚本的响应头，默认包含链路 ID 与限流额度头。
         */
        private List<String> exposedHeaders = new ArrayList<>(
                List.of("kmc-trace-id", "X-Response-Time", "X-RateLimit-Remaining", "X-RateLimit-Burst-Capacity",
                        "X-RateLimit-Replenish-Rate"));

        private boolean allowCredentials = false;

        private Duration maxAge = Duration.ofHours(1);
    }

    /**
     * 统一鉴权：由网关校验 UAA 签发的 JWT，下游服务只信任网关透传的身份头。
     */
    @Data
    public static class Auth {

        /**
         * 关闭时所有请求放行（本地联调用），开启后通过 Nacos 服务发现解析 UAA 的
         * {@code /oauth2/jwks}（见 {@code kuma.boot.security.oauth2.endpoint.uaa-service-name}）。
         */
        private boolean enabled = false;

        /**
         * 免鉴权路径（Ant 风格），登录 / 令牌 / 健康检查 / 文档等必须放行。
         */
        private List<String> whiteList = new ArrayList<>(List.of(
                "/actuator/**",
                "/fallback/**",
                "/uaa/**",
                "/doc.html",
                "/webjars/**",
                "/v3/api-docs/**",
                "/favicon.ico"));

        /**
         * JWT 中承载权限的 claim 名称，UAA 默认写入 {@code authorities}。
         */
        private String authoritiesClaim = "authorities";

        /**
         * JWT 中承载用户名的 claim 名称，缺失时回退到 {@code sub}。
         */
        private String usernameClaim = "preferred_username";

        /**
         * 是否把鉴权结果透传给下游（X-User-Id / X-User-Name / X-User-Authorities / X-Client-Id）。
         */
        private boolean relayIdentity = true;
    }

    /**
     * IP 访问控制。名单项支持精确 IP（{@code 10.0.0.1}）与 CIDR（{@code 10.0.0.0/8}）。
     */
    @Data
    public static class Access {

        private boolean enabled = false;

        /**
         * 非空时只放行名单内 IP，用于内网管理入口。
         */
        private List<String> whiteList = new ArrayList<>();

        private List<String> blackList = new ArrayList<>();
    }

    /**
     * 访问日志。
     */
    @Data
    public static class Log {

        private boolean enabled = true;

        /**
         * 超过该耗时的请求以 WARN 记录，便于直接在日志里筛慢接口。
         */
        private Duration slowThreshold = Duration.ofSeconds(2);

        /**
         * 不记录日志的路径，默认屏蔽健康检查等高频噪声。
         */
        private List<String> excludePaths =
                new ArrayList<>(List.of("/actuator/health/**", "/actuator/prometheus", "/favicon.ico"));

        /**
         * 是否在响应中回写 {@code X-Response-Time} 头。
         */
        private boolean responseTimeHeader = true;
    }

    /**
     * 限流键策略。限流本身由 Gateway 内置的 {@code RequestRateLimiter} 过滤器在路由上声明，
     * 这里只决定 {@code userOrIpKeyResolver} 如何计算限流维度。
     */
    @Data
    public static class RateLimit {

        /**
         * 匿名请求（无 JWT）时是否按 IP 限流；false 表示匿名请求不限流。
         */
        private boolean fallbackToIp = true;

        /**
         * 是否把请求路径纳入限流键，使不同接口各自独立计额。
         */
        private boolean includePath = false;
    }
}
