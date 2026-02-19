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

package com.kuma.boot.data.mybatis.interceptor.tenant;

import com.kuma.boot.data.mybatis.interceptor.tenant.handler.TenantInfoHandler;
import com.kuma.boot.data.mybatis.interceptor.tenant.parser.DefaultSqlParser;
import com.kuma.boot.data.mybatis.interceptor.tenant.service.ITenantService;
import java.util.Map;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * 共享数据库的多租户系统 MyBatis sql 拦截器
 *
 * @author wency_cai
 */
@Intercepts({
        @Signature(
                type = Executor.class,
                method = "update",
                args = {MappedStatement.class, Object.class}),
        @Signature(
                type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(
                type = Executor.class,
                method = "query",
                args = {
                        MappedStatement.class,
                        Object.class,
                        RowBounds.class,
                        ResultHandler.class,
                        CacheKey.class,
                        BoundSql.class
                }),
})
public class MybatisMultiTenantPluginInterceptor implements Interceptor {

    private static final DefaultSqlParser DEFAULT_SQL_PARSER = new DefaultSqlParser();

    public MybatisMultiTenantPluginInterceptor(
            ITenantService tenantService, TenantInfoHandler tenantInfoHandler) {
        DEFAULT_SQL_PARSER.setTenantInfoHandler(tenantInfoHandler);
        DEFAULT_SQL_PARSER.setTenantService(tenantService);
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        if (DEFAULT_SQL_PARSER.isIgnoreDynamicDatasource()) {
            return invocation.proceed();
        }

        Object target = invocation.getTarget();
        if (target instanceof Executor) {
            MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
            Object param1 = invocation.getArgs()[1];
            BoundSql boundSql = ms.getBoundSql(param1);

            // 根据方法忽略多租户字段
            if (TenantInfoHandler.IGNORE_TENANT_ID_METHODS.contains(ms.getId())) {
                return invocation.proceed();
            }
            // 获取mapper 接口上租户字段参数值
            Object paramTenantId = getParamTenantId(param1);
            // 获取原始sql
            String sql = boundSql.getSql();
            // 得到修改后的sql
            sql = DEFAULT_SQL_PARSER.setTenantParameter(sql, paramTenantId);
            BoundSql newBoundSql =
                    new BoundSql(
                            ms.getConfiguration(),
                            sql,
                            boundSql.getParameterMappings(),
                            boundSql.getParameterObject());
            MappedStatement newMs = buildMappedStatement(ms, new BoundSqlSqlSource(newBoundSql));

            for (ParameterMapping mapping : boundSql.getParameterMappings()) {
                String prop = mapping.getProperty();
                if (boundSql.hasAdditionalParameter(prop)) {
                    newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
                }
            }

            // 替换 MappedStatement
            invocation.getArgs()[0] = newMs;
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor || target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    /**
     * 根据已有MappedStatement构造新的MappedStatement
     */
    private MappedStatement buildMappedStatement(MappedStatement ms, SqlSource sqlSource) {
        MappedStatement.Builder builder =
                new MappedStatement.Builder(
                        ms.getConfiguration(), ms.getId(), sqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }

    private Object getParamTenantId(Object param1) {
        if (param1 instanceof Map) {
            TenantInfoHandler tenantInfoHandler = DEFAULT_SQL_PARSER.getTenantInfoHandler();
            String tenantIdColumn = tenantInfoHandler.getTenantIdColumn();
            Map<?, ?> paramMap = (Map<?, ?>) param1;
            return paramMap.getOrDefault(tenantIdColumn, null);
        }
        return null;
    }

    /**
     * 用于构造新MappedStatement
     */
    public static class BoundSqlSqlSource implements SqlSource {

        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
}
