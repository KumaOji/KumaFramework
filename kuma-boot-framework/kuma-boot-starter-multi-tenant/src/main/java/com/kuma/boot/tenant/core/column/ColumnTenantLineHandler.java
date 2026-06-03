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

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.kuma.boot.tenant.core.TenantIdProvider;
import com.kuma.boot.tenant.properties.TenantProperties;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;

/**
 * 字段隔离模式租户处理器
 *
 * <p>实现 MybatisPlus {@link TenantLineHandler} SPI，向所有非忽略表的
 * SELECT / INSERT / UPDATE / DELETE SQL 自动注入租户过滤条件：
 * {@code WHERE tenant_id = 'xxx'}。
 *
 * <p>租户 ID 由 {@link TenantIdProvider} 获取，默认读取线程本地变量
 * {@link com.kuma.boot.common.holder.TenantContextHolder}。
 *
 * <p>租户 ID 类型自适应：优先作为 {@code BIGINT} 字面量处理；若非纯数字则降级为字符串字面量，
 * 兼容 UUID 等非数字租户标识。
 *
 * @author kuma
 * @version 2026.01
 * @since 2026-01-01
 */
public class ColumnTenantLineHandler implements TenantLineHandler {

    private final TenantIdProvider tenantIdProvider;
    private final TenantProperties properties;

    public ColumnTenantLineHandler(TenantIdProvider tenantIdProvider, TenantProperties properties) {
        this.tenantIdProvider = tenantIdProvider;
        this.properties = properties;
    }

    @Override
    public Expression getTenantId() {
        String tenantId = tenantIdProvider.getTenantId();
        if (tenantId == null) {
            return new NullValue();
        }
        try {
            return new LongValue(tenantId);
        } catch (NumberFormatException e) {
            return new StringValue(tenantId);
        }
    }

    @Override
    public String getTenantIdColumn() {
        return properties.getColumnName();
    }

    @Override
    public boolean ignoreTable(String tableName) {
        return properties.getIgnoreTables().contains(tableName);
    }
}
