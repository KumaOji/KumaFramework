/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.mybatis.config;

import com.kuma.boot.datasource.context.SchemaContext;
import com.kuma.boot.datasource.utils.DataSourceUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Schema 切换拦截器
 * 在执行 SQL 前动态切换 schema
 * - MySQL: USE `database`
 * - PostgreSQL: SET search_path TO schema
 * 使用方式：请求头 X-Schema，或 {@link SchemaContext#setSchema(String)} / {@link SchemaContext#withSchema(String, Runnable)}
 *
 * @author kuma
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class SchemaSwitchInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(SchemaSwitchInterceptor.class);

    private boolean enabled = true;
    private String defaultSchema;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (!enabled) {
            return invocation.proceed();
        }

        Executor executor = (Executor) invocation.getTarget();
        Connection connection;
        try {
            connection = executor.getTransaction().getConnection();
        } catch (SQLException e) {
            log.warn("获取连接失败，跳过 schema 切换: {}", e.getMessage());
            return invocation.proceed();
        }
        if (connection == null || connection.isClosed()) {
            return invocation.proceed();
        }

        String targetSchema = SchemaContext.hasSchema() ? SchemaContext.getSchema() : defaultSchema;
        if (targetSchema != null) {
            targetSchema = targetSchema.trim();
        }
        if (targetSchema == null || targetSchema.isEmpty()) {
            return invocation.proceed();
        }

        boolean switched = false;
        String current = getCurrentSchema(connection);
        if (!targetSchema.equals(current)) {
            try {
                switchSchema(connection, targetSchema);
                switched = true;
                log.debug("切换 schema: {} -> {}", current, targetSchema);
            } catch (SQLException e) {
                log.error("切换 schema 失败: {} - {}", targetSchema, e.getMessage(), e);
            }
        }

        try {
            return invocation.proceed();
        } finally {
            if (switched && defaultSchema != null && !defaultSchema.isBlank()) {
                try {
                    switchSchema(connection, defaultSchema);
                    log.debug("恢复 schema: {}", defaultSchema);
                } catch (SQLException e) {
                    log.warn("恢复 schema 失败: {}", e.getMessage());
                }
            }
        }
    }

    private void switchSchema(Connection connection, String schema) throws SQLException {
        String url = connection.getMetaData().getURL();
        String sql;
        if (DataSourceUtils.isPostgresqlUrl(url)) {
            sql = "SET search_path TO " + quoteIdentifier(schema, '"');
        } else {
            sql = "USE `" + schema.replace("`", "``") + "`";
        }
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    private String getCurrentSchema(Connection connection) throws SQLException {
        String url = connection.getMetaData().getURL();
        String query = DataSourceUtils.isPostgresqlUrl(url) ? "SELECT current_schema()" : "SELECT DATABASE()";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next() ? rs.getString(1) : null;
        }
    }

    private static String quoteIdentifier(String name, char quote) {
        return quote + name.replace(String.valueOf(quote), String.valueOf(quote) + quote) + quote;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties props) {
        String enabledStr = props.getProperty("enabled", "true");
        this.enabled = Boolean.parseBoolean(enabledStr);
        String ds = props.getProperty("defaultSchema");
        if (ds != null && !ds.trim().isEmpty()) {
            this.defaultSchema = ds.trim();
        }
    }

    public void setDefaultSchema(String defaultSchema) {
        this.defaultSchema = defaultSchema == null ? null : defaultSchema.trim();
    }
}
