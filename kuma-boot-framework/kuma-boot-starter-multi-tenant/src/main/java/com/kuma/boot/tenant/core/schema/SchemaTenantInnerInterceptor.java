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

package com.kuma.boot.tenant.core.schema;

import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.kuma.boot.common.holder.TenantContextHolder;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mybatis.mybatisplus.ReplaceSql;
import com.kuma.boot.tenant.core.TenantIdProvider;
import com.kuma.boot.tenant.properties.TenantProperties;
import java.sql.Connection;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * Schema 隔离模式 MybatisPlus 内部拦截器
 *
 * <p>在 SQL 执行前将所有表名替换为 {@code {schemaDatabasePrefix}_{tenantId}.tableName}，
 * 实现每个租户独立 Schema 的物理隔离。
 *
 * <p>依赖 {@link ReplaceSql} 进行 SQL 改写（基于 Druid SQL 解析器），支持 SELECT / INSERT /
 * UPDATE / DELETE，以及子查询、JOIN、UNION 等复杂语句。
 *
 * <p>若当前线程无租户上下文（{@link TenantIdProvider#getTenantId()} 返回 null），
 * SQL 保持原样，不做任何替换。
 *
 * @author kuma
 * @version 2026.01
 * @since 2026-01-01
 */
public class SchemaTenantInnerInterceptor implements InnerInterceptor {

    private final TenantIdProvider tenantIdProvider;
    private final TenantProperties properties;

    public SchemaTenantInnerInterceptor(
            TenantIdProvider tenantIdProvider, TenantProperties properties) {
        this.tenantIdProvider = tenantIdProvider;
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
        // 统一在 beforePrepare 处理，避免 SQL 被重复改写
    }

    @Override
    public void beforePrepare(
            StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        SqlCommandType sct = ms.getSqlCommandType();
        if (sct != SqlCommandType.SELECT
                && sct != SqlCommandType.INSERT
                && sct != SqlCommandType.UPDATE
                && sct != SqlCommandType.DELETE) {
            return;
        }
        if (InterceptorIgnoreHelper.willIgnoreDynamicTableName(ms.getId())) {
            return;
        }

        String tenantId = tenantIdProvider.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            return;
        }

        String schemaName = buildSchemaName(tenantId);
        PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
        LogUtils.debug("[SchemaTenant] 原始 SQL: {}", mpBs.sql());
        mpBs.sql(ReplaceSql.replaceSql(properties.getDbType(), schemaName, mpBs.sql()));
        LogUtils.debug("[SchemaTenant] 改写后 SQL: {}", mpBs.sql());
    }

    private String buildSchemaName(String tenantId) {
        String schema = properties.getSchemaDatabasePrefix() + "_" + tenantId;
        String owner = properties.getSchemaOwner();
        if (owner != null && !owner.isBlank()) {
            schema = schema + "." + owner;
        }
        return schema;
    }
}
