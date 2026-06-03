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

import com.kuma.boot.common.holder.TenantContextHolder;

/**
 * 默认租户 ID 提供者
 *
 * <p>从 {@link TenantContextHolder} 线程本地变量中读取当前租户 ID。
 * 租户 ID 由 {@link com.kuma.boot.tenant.web.TenantContextFilter} 在请求入口处写入。
 *
 * @author kuma
 * @version 2026.01
 * @since 2026-01-01
 */
public class DefaultTenantIdProvider implements TenantIdProvider {

    @Override
    public String getTenantId() {
        return TenantContextHolder.getTenant();
    }
}
