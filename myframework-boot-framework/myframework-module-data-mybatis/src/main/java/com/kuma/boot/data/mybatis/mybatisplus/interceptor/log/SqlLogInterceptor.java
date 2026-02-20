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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.log;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.SystemClock;
import com.kuma.boot.common.utils.log.LogUtils;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;

/**
 * sql日志记录器
 *
 * @author kuma
 * @version 2022.03
 * @since 2021/10/30 22:01
 */
// @Intercepts({
// 该方法会在所有 SELECT 查询方法执行时被调用 通过这个接口参数可以获取很多有用的信息，这也是最常被拦截的方法。
//	@Signature(
//		method = "query",
//		type = Executor.class,
//		args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
// 该方法会在所有 SELECT 查询方法执行时被调用 通过这个接口参数可以获取很多有用的信息，这也是最常被拦截的方法。
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
// 该方法只有在查询 的返回值类型为 Cursor 时被调用
//	@Signature(
//		method = "queryCursor",
//		type = Executor.class,
//		args = {
//			MappedStatement.class,
//			Object.class,
//			RowBounds.class,
//		}),
// 该方法会在所有的 INSERT、UPDATE、DELETE 执行时被调用，因此如果想要拦截这类操作，可以拦截该方法
//	@Signature(
//		method = "update",
//		type = Executor.class,
//		args = {MappedStatement.class, Object.class}),
//
// @Intercepts({
// 该方法会在数据库执行前被调用 优先于当前接口中的其他方法而被执行
//        @Signature(type = StatementHandler.class, method = "prepare", args =
// {Connection.class,Integer.class}),
// 该方法在 prepare 方法之后执行，用于处理参数信息
//        @Signature(type = StatementHandler.class, method = "parameterize", args =
// {Statement.class}),
// 在全局设置配置 defaultExecutorType BATCH 时，执行数据操作才会调用该方法
//        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class}),
// 执行UPDATE、DELETE、INSERT方法时执行
//        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
// 执行 SELECT 方法时调用，接口方法对应的签名如下。
//        @Signature(type = StatementHandler.class, method = "query", args =
// {Statement.class,ResultHandler.class}),
//        @Signature(type = StatementHandler.class, method = "queryCursor", args =
// {Statement.class}),
// 获取实际的SQL字符串
//        @Signature(type = StatementHandler.class, method = "getBoundSql", args = {}),
//        @Signature(type = StatementHandler.class, method = "getParameterHandler", args = {})
// }
//  @Intercepts({
// 该方法只在执行存储过程处理出参的时候被调用
//        @Signature(type = ParameterHandler.class, method = "getParameterObject", args = {}),
// 该方法在所有数据库方法设置 SQL 参数时被调用
//        @Signature(type = ParameterHandler.class, method = "setParameters", args =
// {PreparedStatement.class})
// })
//
// @Intercepts({
// 该方法会在除存储过程及返回值类型为 Cursor 以外的查询方法中被调用。
//        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args =
// {Statement.class}),
// 只会在返回值类型为 ursor 查询方法中被调用
//        @Signature(type = ResultSetHandler.class, method = "handleCursorResultSets", args =
// {Statement.class}),
// 只在使用存储过程处理出参时被调用
//        @Signature(type = ResultSetHandler.class, method = "handleOutputParameters", args =
// {CallableStatement.class})
// })
@Intercepts({
        @Signature(
                type = StatementHandler.class,
                method = "query",
                args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = Statement.class),
        @Signature(type = StatementHandler.class, method = "batch", args = Statement.class)
})
public class SqlLogInterceptor implements Interceptor {

    private static final String DRUID_POOLED_PREPARED_STATEMENT =
            "com.alibaba.druid.pool.DruidPooledPreparedStatement";
    private static final String T4C_PREPARED_STATEMENT = "oracle.jdbc.driver.T4CPreparedStatement";
    private static final String ORACLE_PREPARED_STATEMENT_WRAPPER =
            "oracle.jdbc.driver.OraclePreparedStatementWrapper";

    private Method oracleGetOriginalSqlMethod;
    private Method druidGetSqlMethod;

    // 执行拦截逻辑的方法
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // target对应我们拦截的Executor对象
        // method对应Executor#update方法
        // args对应Executor#update#args参数
        Object target1 = invocation.getTarget();
        Method method = invocation.getMethod();
        Object[] args = invocation.getArgs();

        Statement statement;
        Object firstArg = invocation.getArgs()[0];
        if (Proxy.isProxyClass(firstArg.getClass())) {
            statement = (Statement) (SystemMetaObject.forObject(firstArg).getValue("h.statement"));
        } else {
            statement = (Statement) firstArg;
        }
        MetaObject stmtMetaObj = SystemMetaObject.forObject(statement);
        try {
            statement = (Statement) stmtMetaObj.getValue("stmt.statement");
        } catch (Exception e) {
            // do nothing
        }

        if (stmtMetaObj.hasGetter("delegate")) {
            // Hikari
            try {
                statement = (Statement) stmtMetaObj.getValue("delegate");
            } catch (Exception ignored) {

            }
        }

        String originalSql = null;
        String stmtClassName = statement.getClass().getName();
        if (DRUID_POOLED_PREPARED_STATEMENT.equals(stmtClassName)) {
            try {
                if (druidGetSqlMethod == null) {
                    Class<?> clazz = Class.forName(DRUID_POOLED_PREPARED_STATEMENT);
                    druidGetSqlMethod = clazz.getMethod("getSql");
                }
                Object stmtSql = druidGetSqlMethod.invoke(statement);
                if (stmtSql instanceof String) {
                    originalSql = (String) stmtSql;
                }
            } catch (Exception e) {
                LogUtils.error(e);
            }
        } else if (T4C_PREPARED_STATEMENT.equals(stmtClassName)
                || ORACLE_PREPARED_STATEMENT_WRAPPER.equals(stmtClassName)) {
            try {
                if (oracleGetOriginalSqlMethod != null) {
                    Object stmtSql = oracleGetOriginalSqlMethod.invoke(statement);
                    if (stmtSql instanceof String) {
                        originalSql = (String) stmtSql;
                    }
                } else {
                    Class<?> clazz = Class.forName(stmtClassName);
                    oracleGetOriginalSqlMethod = getMethodRegular(clazz, "getOriginalSql");
                    if (oracleGetOriginalSqlMethod != null) {
                        // OraclePreparedStatementWrapper is not a public class, need set this.
                        oracleGetOriginalSqlMethod.setAccessible(true);
                        if (null != oracleGetOriginalSqlMethod) {
                            Object stmtSql = oracleGetOriginalSqlMethod.invoke(statement);
                            if (stmtSql instanceof String) {
                                originalSql = (String) stmtSql;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // ignore
            }
        }
        if (originalSql == null) {
            originalSql = statement.toString();
        }
        originalSql = originalSql.replaceAll("[\\s]+", StringPool.SPACE);
        int index = indexOfSqlStart(originalSql);
        if (index > 0) {
            originalSql = originalSql.substring(index);
        }

        // 计算执行 SQL 耗时
        long start = SystemClock.now();
        Object result = invocation.proceed();
        long timing = SystemClock.now() - start;

        // SQL 打印执行结果
        Object target = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(target);
        MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");

        // 打印 sql
        String sqlLogger =
                "\n\n==============  Sql Start  =============="
                        + "\nExecute ID  ：{}"
                        + "\nExecute SQL ：{}"
                        + "\nExecute Time：{} ms"
                        + "\n==============  Sql  End   ==============\n";
        LogUtils.info(sqlLogger, ms.getId(), originalSql, timing);
        return result;
    }

    // 这个方法的参数 target 就是拦截器要拦截的对象，该方法会在创建被拦截的接口实现类时被调用。
    // 该方法的实现很简单 ，只需要调用 MyBatis 提供的 Plug 类的 wrap 静态方法就可以通过 Java 动态代理拦截目标对象
    // 那就是Mybatis在创建拦截器代理时候会判断一次，当前这个类 Interceptor 到底需不需要生成一个代理进行拦截，
    // 如果需要拦截，就生成一个代理对象，这个代理就是一个 {@link Plugin}，它实现了jdk的动态代理接口 {@link InvocationHandler}，
    // 如果不需要代理，则直接返回目标对象本身 加载时机：该方法在 mybatis 加载核心配置文件时被调用
    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    // 这个方法用来传递插件的参数，可以通过参数来改变插件的行为
    @Override
    public void setProperties(Properties properties) {
        // NOP
    }

    /**
     * 获取此方法名的具体 Method
     *
     * @param clazz class 对象
     * @param methodName 方法名
     * @return 方法
     */
    private Method getMethodRegular(Class<?> clazz, String methodName) {
        if (Object.class.equals(clazz)) {
            return null;
        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return getMethodRegular(clazz.getSuperclass(), methodName);
    }

    /**
     * 获取sql语句开头部分
     *
     * @param sql ignore
     * @return ignore
     */
    private int indexOfSqlStart(String sql) {
        String upperCaseSql = sql.toUpperCase();
        Set<Integer> set = new HashSet<>();
        set.add(upperCaseSql.indexOf("SELECT "));
        set.add(upperCaseSql.indexOf("UPDATE "));
        set.add(upperCaseSql.indexOf("INSERT "));
        set.add(upperCaseSql.indexOf("DELETE "));
        set.remove(-1);
        if (CollectionUtils.isEmpty(set)) {
            return -1;
        }
        List<Integer> list = new ArrayList<>(set);
        list.sort(Comparator.naturalOrder());
        return list.get(0);
    }
}
