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

package com.kuma.boot.data.mybatis.interceptor.sqlanalysis.replace;

import java.util.Arrays;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author huhaitao21
 * @Description sql替换模块
 * @since 14:25 2023/6/1
 **/
public class SqlReplace {

    private static Logger logger = LoggerFactory.getLogger(SqlReplace.class);

    public static void replace( Invocation invocation, String newSql ) {
        // 获取当前执行的SQL语句
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object parameter = args[1];

        // 生成新sql
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        BoundSql newBoundSql =
                new BoundSql(
                        mappedStatement.getConfiguration(),
                        newSql,
                        boundSql.getParameterMappings(),
                        parameter);
        logger.debug("sql analysis - sql replace old:{}", boundSql.getSql());
        logger.debug("sql analysis - sql replace new:{}", newSql);

        boundSql.getParameterMappings()
                .forEach(
                        ( e ) -> {
                            String prop = e.getProperty();
                            if (boundSql.hasAdditionalParameter(prop)) {
                                newBoundSql.setAdditionalParameter(
                                        prop, boundSql.getAdditionalParameter(prop));
                            }
                        });

        // 把新的查询放到statement里
        MappedStatement newMs =
                copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));
        args[0] = newMs;
    }

    /**
     * 替换sql，生成新的 MappedStatement
     */
    private static MappedStatement copyFromMappedStatement(
            MappedStatement ms, SqlSource newSqlSource ) {
        MappedStatement.Builder builder =
                new MappedStatement.Builder(
                        ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());

        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length > 0) {
            StringBuilder keyPropertiesBuilder = new StringBuilder();
            String[] keyProperties = ms.getKeyProperties();
            Arrays.stream(keyProperties)
                    .forEach(key -> keyPropertiesBuilder.append(key).append(","));
            keyPropertiesBuilder.delete(
                    keyPropertiesBuilder.length() - 1, keyPropertiesBuilder.length());
            builder.keyProperty(keyPropertiesBuilder.toString());
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

    /**
     * BoundSqlSqlSource
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    private static class BoundSqlSqlSource implements SqlSource {

        private BoundSql boundSql;

        public BoundSqlSqlSource( BoundSql boundSql ) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql( Object parameterObject ) {
            return boundSql;
        }
    }
}
