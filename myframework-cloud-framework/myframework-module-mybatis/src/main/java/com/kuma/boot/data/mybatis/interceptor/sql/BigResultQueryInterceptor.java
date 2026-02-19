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

import cn.hutool.core.util.ReflectUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import java.lang.reflect.Proxy;
import java.sql.Statement;
import java.util.List;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.logging.jdbc.PreparedStatementLogger;
import org.apache.ibatis.logging.jdbc.StatementLogger;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

/**
 * 自定义监控查询数据数量拦截器
 */
@Intercepts({
        @Signature(
                type = ResultSetHandler.class,
                method = "handleResultSets",
                args = {Statement.class})
})
public class BigResultQueryInterceptor implements Interceptor {
    private Boolean flag;
    private Integer threshold;

    public BigResultQueryInterceptor() {}

    public BigResultQueryInterceptor(Boolean flag, Integer threshold) {
        this.flag = flag;
        this.threshold = threshold;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        Object result = invocation.proceed();
        String sql = "";
        try {
            if (flag) {
                Statement statement = (Statement) invocation.getArgs()[0];
                // mybatis设置了logging时启动的代理机制
                if (Proxy.isProxyClass(statement.getClass())) {
                    MetaObject metaObject = SystemMetaObject.forObject(statement);
                    Object h = metaObject.getValue("h");
                    if (h instanceof StatementLogger) {
                        RoutingStatementHandler rsh =
                                (RoutingStatementHandler) invocation.getTarget();
                        sql = rsh.getBoundSql().getSql();
                    } else if (h instanceof PreparedStatementLogger) {
                        PreparedStatementLogger psl = (PreparedStatementLogger) h;
                        sql = (String) ReflectUtil.getFieldValue(psl.getPreparedStatement(), "sql");
                    }
                } else {
                    // if (statement instanceof ShardingPreparedStatement) {
                    //	sql = (String) ReflectUtil.getFieldValue(statement, "sql");
                    // } else {
                    //	sql = statement.toString();
                    // }
                    sql = statement.toString();
                }
                if (result instanceof List && ((List<?>) result).size() >= threshold) {
                    LogUtils.warn("数据库查询结果集 {} 条，存在内存泄露风险，请使用分页查询。", ((List) result).size());
                }
            }
        } catch (Exception e) {
        }
        return result;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof ResultSetHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }
}
