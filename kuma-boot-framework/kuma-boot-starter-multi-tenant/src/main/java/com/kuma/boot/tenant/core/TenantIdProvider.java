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

package com.kuma.boot.tenant.core;

/**
 * 当前租户 ID 提供者 SPI
 *
 * <p>框架内置 {@link DefaultTenantIdProvider}，从 {@link com.kuma.boot.common.holder.TenantContextHolder}
 * 读取租户 ID。业务侧可注册自定义实现以支持从 JWT、Session、数据库缓存等来源获取租户 ID：
 *
 * <pre>{@code
 * @Bean
 * public TenantIdProvider tenantIdProvider(JwtUtils jwt) {
 *     return () -> jwt.getCurrentUser().getTenantId();
 * }
 * }</pre>
 *
 * @author kuma
 * @version 2026.01
 * @since 2026-01-01
 */
@FunctionalInterface
public interface TenantIdProvider {

    /**
     * 返回当前请求/线程上下文中的租户 ID。
     *
     * @return 租户 ID 字符串；若无上下文则返回 {@code null}
     */
    String getTenantId();
}
