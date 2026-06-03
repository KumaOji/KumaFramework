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

package com.kuma.boot.tenant.annotation;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 忽略多租户过滤
 *
 * <p>标注在 Mapper 方法或 Mapper 类上，使该方法/类下的所有 SQL 跳过租户隔离拦截（COLUMN 和 SCHEMA 模式均生效）。
 *
 * <p>示例（Mapper 方法）：
 *
 * <pre>{@code
 * @IgnoreTenant
 * List<User> listAllTenantUsers();
 * }</pre>
 *
 * <p>示例（Mapper 类）：
 *
 * <pre>{@code
 * @IgnoreTenant
 * public interface SysMenuMapper extends BaseMapper<SysMenu> { }
 * }</pre>
 *
 * <p>若需在 Service 层临时绕过，可调用 {@link com.kuma.boot.common.holder.TenantContextHolder#clear()}
 * 清除租户上下文，操作完成后务必重新设置。
 *
 * @author kuma
 * @version 2026.01
 * @since 2026-01-01
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public @interface IgnoreTenant {
}
