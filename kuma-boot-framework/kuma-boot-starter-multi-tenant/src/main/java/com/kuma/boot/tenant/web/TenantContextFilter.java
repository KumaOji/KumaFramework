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

package com.kuma.boot.tenant.web;

import com.kuma.boot.common.holder.TenantContextHolder;
import com.kuma.boot.tenant.properties.TenantProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 租户上下文 HTTP 过滤器
 *
 * <p>在每次 HTTP 请求进入时，从请求头（默认 {@code X-Tenant-Id}）中提取租户 ID，
 * 并写入 {@link TenantContextHolder}；请求结束后自动清除，防止线程池复用导致的上下文泄漏。
 *
 * <p>客户端使用示例：
 *
 * <pre>
 * GET /api/users HTTP/1.1
 * X-Tenant-Id: 1001
 * Authorization: Bearer eyJhbGc...
 * </pre>
 *
 * <p>如需从其他来源（JWT Payload、Session、数据库）提取租户 ID，可注册自定义 Bean 覆盖本过滤器：
 *
 * <pre>{@code
 * @Bean
 * public FilterRegistrationBean<TenantContextFilter> tenantContextFilterRegistration() {
 *     return ...; // 自定义注册
 * }
 * }</pre>
 *
 * @author kuma
 * @version 2026.01
 * @since 2026-01-01
 */
public class TenantContextFilter extends OncePerRequestFilter {

    private final TenantProperties properties;

    public TenantContextFilter(TenantProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String tenantId = request.getHeader(properties.getHeaderName());
            if (tenantId != null && !tenantId.isBlank()) {
                TenantContextHolder.setTenant(tenantId.trim());
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContextHolder.clear();
        }
    }
}
