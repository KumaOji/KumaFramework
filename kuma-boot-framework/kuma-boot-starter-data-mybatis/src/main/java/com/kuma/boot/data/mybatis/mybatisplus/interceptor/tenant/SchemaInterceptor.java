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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.tenant;

import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.kuma.boot.common.holder.TenantContextHolder;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mybatis.mybatisplus.ReplaceSql;
import com.kuma.boot.data.mybatis.autoconfigure.properties.MybatisPlusInterceptorProperties;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import cn.hutool.core.util.StrUtil;

/** SCHEMA模式插件 */
public class SchemaInterceptor implements InnerInterceptor {

    private final MybatisPlusInterceptorProperties.MultiTenant tenantProperties;

    public SchemaInterceptor(MybatisPlusInterceptorProperties.MultiTenant tenantProperties) {
        this.tenantProperties = tenantProperties;
    }

    protected String changeTable(String sql) {
        // 想要 执行sql时， 不切换到 lamp_base_{TENANT} 库, 请直接返回null
        String tenantCode = TenantContextHolder.getTenant();
        String database = TenantContextHolder.getDatabase();
        if (StrUtil.isEmpty(tenantCode)) {
            return sql;
        }

        String schemaName =
                StrUtil.format(
                        "{}_{}",
                        StrUtil.isEmpty(database)
                                ? tenantProperties.getTenantDatabasePrefix()
                                : database,
                        tenantCode);
        if (StrUtil.isNotEmpty(tenantProperties.getOwner())) {
            schemaName += "." + tenantProperties.getOwner();
        }
        // 想要 执行sql时， 切换到 切换到自己指定的库， 直接修改 setSchemaName
        return ReplaceSql.replaceSql(tenantProperties.getDbType(), schemaName, sql);
    }

    @Override
    public void beforeQuery(
            Executor executor,
            MappedStatement ms,
            Object parameter,
            RowBounds rowBounds,
            ResultHandler resultHandler,
            BoundSql boundSql)
            throws SQLException {
        // 统一交给 beforePrepare 处理,防止某些sql解析不到，又被beforePrepare重复处理
    }

    @Override
    public void beforePrepare(
            StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        SqlCommandType sct = ms.getSqlCommandType();
        if (sct == SqlCommandType.INSERT
                || sct == SqlCommandType.UPDATE
                || sct == SqlCommandType.DELETE
                || sct == SqlCommandType.SELECT) {
            if (InterceptorIgnoreHelper.willIgnoreDynamicTableName(ms.getId())) {
                return;
            }
            PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
            LogUtils.debug("未替换前的sql: {}", mpBs.sql());
            mpBs.sql(this.changeTable(mpBs.sql()));
        }
        TenantContextHolder.clearDatabase();
    }
}
