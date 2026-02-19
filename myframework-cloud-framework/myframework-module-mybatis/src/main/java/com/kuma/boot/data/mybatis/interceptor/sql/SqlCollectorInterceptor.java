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

package com.kuma.boot.data.mybatis.interceptor.sql;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.support.Collector;
import java.lang.reflect.Method;
import java.util.Properties;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * sql收集
 *
 * @Intercepts    // 描述：标志该类是一个拦截器
 * @Signature    // 描述：指明该拦截器需要拦截哪一个接口的哪一个方法
 *
 * 四种接口类型
 * Executor: 拦截执行器的方法
 * StatementHandler: 拦截SQL语法构建处理
 * ParameterHandler: 拦截参数处理
 * ResultSetHandler: 拦截结果集处理
 *
 * Executor ->StatementHandler ->ParameterHandler  ->ResultSetHandler
 *
 * type; // 四种类型接口中的某一个接口，如Executor.class；
 * method; // 对应接口中的某一个方法名，比如Executor的query方法；
 * args; // 对应接口中的某一个方法的参数，比如Executor中query方法因为重载原因，有多个，args就是指明参数类型，从而确定是具体哪一个方法；
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-10 17:10:37
 */
// @Intercepts({
// 所有的query调用的执行
//	@Signature(
//		method = "query",
//		type = Executor.class,
//		args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
//  所有的query调用的执行
//	@Signature(
//		method = "query",
//		type = Executor.class,
//		args = {
//			MappedStatement.class,
//			Object.class,
//			RowBounds.class,
//			ResultHandler.class,
//			CacheKey.class,
//			BoundSql.class
//		}),
//  此方法只有在返回cursor时被调用
//	@Signature(
//		method = "queryCursor",
//		type = Executor.class,
//		args = {
//			MappedStatement.class,
//			Object.class,
//			RowBounds.class,
//		}),
// 所有的update/insert/delete调用的执行
//	@Signature(
//		method = "update",
//		type = Executor.class,
//		args = {MappedStatement.class, Object.class}),
//  在数据库执行之前被调用 优先当前接口的其他方法被调用
//	@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class,
// Integer.class}),
// 该方法在perpare方法执行之后被执行 用于处理参数信息
//	@Signature(type = StatementHandler.class, method = "parameterize", args = Statement.class),
// 只有在全局设置defaultExecutorType=batch时 执行数据操作才会调用此方法
//	@Signature(type = StatementHandler.class, method = "batch", args = Statement.class),
// 执行update/delete/insert时调用改方法
//	@Signature(type = StatementHandler.class, method = "update", args = Statement.class),
// 执行select时调用
//	@Signature(type = StatementHandler.class, method = "query", args = {Statement.class,
// ResultHandler.class}),
//	@Signature(type = StatementHandler.class, method = "queryCursor", args = {Statement.class}),
//
//  在执行存储过程处理中出参的时候被调用
//	@Signature(type = ParameterHandler.class, method = "getParameterObject", args = {}),
//  在所有数据库方法设置sql参数时被调用
//	@Signature(type = ParameterHandler.class, method = "setParameters", args =
// {PerpareedStatment.class}),
//
//	返回结果时被调用
//	@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class}),
//	@Signature(type = ResultSetHandler.class, method = "handleCursorResultSets", args =
// {Statement.class})
// })
@Intercepts({
        @Signature(
                method = "query",
                type = Executor.class,
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(
                method = "query",
                type = Executor.class,
                args = {
                        MappedStatement.class,
                        Object.class,
                        RowBounds.class,
                        ResultHandler.class,
                        CacheKey.class,
                        BoundSql.class
                }),
        @Signature(
                method = "update",
                type = Executor.class,
                args = {MappedStatement.class, Object.class})
})
public class SqlCollectorInterceptor implements Interceptor {

    private final Collector collector;

    public SqlCollectorInterceptor(Collector collector) {
        this.collector = collector;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget(); // 被代理对象
        Method method = invocation.getMethod(); // 代理方法
        Object[] args = invocation.getArgs(); // 方法参数

        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        // sql语句
        Object parameter = null;
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }

        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        String sql = boundSql.getSql();

        return collector
                .hook("kmc.monitor.mybatis.sql.hook")
                .run(
                        StringUtils.nullToEmpty(sql).replace("\r", "").replace("\n", ""),
                        () -> {
                            try {
                                return invocation.proceed();
                            } catch (Exception e) {
                                LogUtils.error(e);
                                throw new RuntimeException(e);
                            }
                        });
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            // 调用插件
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {}
}
