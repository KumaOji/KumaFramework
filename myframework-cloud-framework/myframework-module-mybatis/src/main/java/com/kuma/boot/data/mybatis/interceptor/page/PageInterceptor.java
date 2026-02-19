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

package com.kuma.boot.data.mybatis.interceptor.page;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * @Test
 * public void test3() {
 *     UserMapper userMapper = sqlSessionFactory.openSession().getMapper(UserMapper.class);
 *     List<User> list = userMapper.getAllUsersByPage(new RowBounds(1,2));
 *     for (User user : list) {
 *         System.out.println("user = " + user);
 *     }
 * }
 *
 *
 * @Test
 * public void test4() {
 *     UserMapper userMapper = sqlSessionFactory.openSession().getMapper(UserMapper.class);
 *     PageRowBounds pageRowBounds = new PageRowBounds(1, 2);
 *     List<User> list = userMapper.getAllUsersByPage(pageRowBounds);
 *     for (User user : list) {
 *         System.out.println("user = " + user);
 *     }
 *     System.out.println("pageRowBounds.getTotal() = " + pageRowBounds.getTotal());
 * }
 *
 * <select id="getAllUsersByPage" resultType="org.javaboy.mybatis03.model.User">
 *     select * from user
 * </select>
 *
 */
@Intercepts(
        @Signature(
                type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}))
public class PageInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameterObject = args[1];
        RowBounds rowBounds = (RowBounds) args[2];
        if (rowBounds != RowBounds.DEFAULT) {
            Executor executor = (Executor) invocation.getTarget();
            BoundSql boundSql = ms.getBoundSql(parameterObject);
            Field additionalParametersField =
                    BoundSql.class.getDeclaredField("additionalParameters");
            additionalParametersField.setAccessible(true);
            Map<String, Object> additionalParameters =
                    (Map<String, Object>) additionalParametersField.get(boundSql);
            if (rowBounds instanceof com.kuma.boot.data.mybatis.interceptor.page.PageRowBounds) {
                MappedStatement countMs = newMappedStatement(ms, Long.class);
                CacheKey countKey =
                        executor.createCacheKey(
                                countMs, parameterObject, RowBounds.DEFAULT, boundSql);
                String countSql = "select count(*) from (" + boundSql.getSql() + ") temp";
                BoundSql countBoundSql =
                        new BoundSql(
                                ms.getConfiguration(),
                                countSql,
                                boundSql.getParameterMappings(),
                                parameterObject);
                Set<String> keySet = additionalParameters.keySet();
                for (String key : keySet) {
                    countBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
                }
                List<Object> countQueryResult =
                        executor.query(
                                countMs,
                                parameterObject,
                                RowBounds.DEFAULT,
                                (ResultHandler) args[3],
                                countKey,
                                countBoundSql);
                Long count = (Long) countQueryResult.get(0);
                ((com.kuma.boot.data.mybatis.interceptor.page.PageRowBounds) rowBounds).setTotal(count);
            }
            CacheKey pageKey = executor.createCacheKey(ms, parameterObject, rowBounds, boundSql);
            pageKey.update("RowBounds");
            String pageSql =
                    boundSql.getSql()
                            + " limit "
                            + rowBounds.getOffset()
                            + ","
                            + rowBounds.getLimit();
            BoundSql pageBoundSql =
                    new BoundSql(
                            ms.getConfiguration(),
                            pageSql,
                            boundSql.getParameterMappings(),
                            parameterObject);
            Set<String> keySet = additionalParameters.keySet();
            for (String key : keySet) {
                pageBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
            }
            List list =
                    executor.query(
                            ms,
                            parameterObject,
                            RowBounds.DEFAULT,
                            (ResultHandler) args[3],
                            pageKey,
                            pageBoundSql);
            return list;
        }
        // 不需要分页，直接返回结果
        return invocation.proceed();
    }

    private MappedStatement newMappedStatement(MappedStatement ms, Class<Long> longClass) {
        MappedStatement.Builder builder =
                new MappedStatement.Builder(
                        ms.getConfiguration(),
                        ms.getId() + "_count",
                        ms.getSqlSource(),
                        ms.getSqlCommandType());
        ResultMap resultMap =
                new ResultMap.Builder(
                        ms.getConfiguration(), ms.getId(), longClass, new ArrayList<>(0))
                        .build();
        builder.resource(ms.getResource())
                .fetchSize(ms.getFetchSize())
                .statementType(ms.getStatementType())
                .timeout(ms.getTimeout())
                .parameterMap(ms.getParameterMap())
                .resultSetType(ms.getResultSetType())
                .cache(ms.getCache())
                .flushCacheRequired(ms.isFlushCacheRequired())
                .useCache(ms.isUseCache())
                .resultMaps(Arrays.asList(resultMap));
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length > 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }
        return builder.build();
    }
}
