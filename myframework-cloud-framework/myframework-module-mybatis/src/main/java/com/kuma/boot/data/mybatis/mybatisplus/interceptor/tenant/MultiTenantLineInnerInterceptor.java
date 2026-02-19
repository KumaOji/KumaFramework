/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.tenant;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.kuma.boot.data.mybatis.autoconfigure.properties.MybatisPlusInterceptorProperties;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * COLUMN 级别多租户拦截器 相对于mybatis-plus 的 TenantLineInnerInterceptor
 *
 * @see TenantLineInnerInterceptor
 */
@SuppressWarnings({"rawtypes"})
public class MultiTenantLineInnerInterceptor extends TenantLineInnerInterceptor {

    private MybatisPlusInterceptorProperties.MultiTenant tenantProperties;

    public MultiTenantLineInnerInterceptor(
            TenantLineHandler tenantLineHandler,
            MybatisPlusInterceptorProperties.MultiTenant tenantProperties) {
        super(tenantLineHandler);
        this.tenantProperties = tenantProperties;
    }

    @Override
    public void beforeQuery(
            Executor executor,
            MappedStatement ms,
            Object parameter,
            RowBounds rowBounds,
            ResultHandler resultHandler,
            BoundSql boundSql) {
        if (isIgnoreMappedStatement(ms.getId())) {
            return;
        }
        super.beforeQuery(executor, ms, parameter, rowBounds, resultHandler, boundSql);
    }

    private boolean isIgnoreMappedStatement(String msId) {
        return tenantProperties.getIgnoreSqls().stream().anyMatch((e) -> e.equalsIgnoreCase(msId));
    }
}
