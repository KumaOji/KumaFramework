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

package com.kuma.boot.tenant.core.column;

import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.kuma.boot.tenant.properties.TenantProperties;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * 字段隔离模式 MybatisPlus 内部拦截器
 *
 * <p>扩展 {@link TenantLineInnerInterceptor}，额外支持按 MappedStatement ID 粒度跳过租户过滤
 * （通过 {@link TenantProperties#getIgnoreSqls()} 配置），适用于无法在 Mapper 方法上添加
 * {@link com.kuma.boot.tenant.annotation.IgnoreTenant} 的第三方 Mapper。
 *
 * @author kuma
 * @version 2026.01
 * @since 2026-01-01
 */
@SuppressWarnings("rawtypes")
public class ColumnTenantInnerInterceptor extends TenantLineInnerInterceptor {

    private final TenantProperties properties;

    public ColumnTenantInnerInterceptor(
            ColumnTenantLineHandler handler, TenantProperties properties) {
        super(handler);
        this.properties = properties;
    }

    @Override
    public void beforeQuery(
            Executor executor,
            MappedStatement ms,
            Object parameter,
            RowBounds rowBounds,
            ResultHandler resultHandler,
            BoundSql boundSql) {
        if (isIgnored(ms.getId())) {
            return;
        }
        super.beforeQuery(executor, ms, parameter, rowBounds, resultHandler, boundSql);
    }

    private boolean isIgnored(String msId) {
        return properties.getIgnoreSqls().stream().anyMatch(msId::equalsIgnoreCase);
    }
}
