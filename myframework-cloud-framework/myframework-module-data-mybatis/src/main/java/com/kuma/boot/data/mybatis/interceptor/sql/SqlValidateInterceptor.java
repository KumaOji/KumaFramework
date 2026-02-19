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

import com.kuma.boot.common.utils.log.LogUtils;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.antlr.v4.runtime.CharStreams;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.SqlParserImplFactory;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
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
 * java校验SQL语句的合法性
 *
 * @author kuma
 * @version 2021.9
 * @Intercepts // 描述：标志该类是一个拦截器
 * @Signature // 描述：指明该拦截器需要拦截哪一个接口的哪一个方法
 * <p>
 * 四种接口类型 Executor: 拦截执行器的方法 StatementHandler: 拦截SQL语法构建处理 ParameterHandler: 拦截参数处理
 * ResultSetHandler: 拦截结果集处理
 * <p>
 * Executor ->StatementHandler ->ParameterHandler  ->ResultSetHandler
 * <p>
 * type; // 四种类型接口中的某一个接口，如Executor.class； method; // 对应接口中的某一个方法名，比如Executor的query方法； args; //
 * 对应接口中的某一个方法的参数，比如Executor中query方法因为重载原因，有多个，args就是指明参数类型，从而确定是具体哪一个方法；
 * @since 2021-09-10 17:10:37
 */
@Intercepts({
        // 所有的query调用的执行
        @Signature(
                method = "query",
                type = Executor.class,
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        // 所有的query调用的执行
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
        // 此方法只有在返回cursor时被调用
        @Signature(
                method = "queryCursor",
                type = Executor.class,
                args = {
                        MappedStatement.class,
                        Object.class,
                        RowBounds.class,
                }),
        // 所有的update/insert/delete调用的执行
        @Signature(
                method = "update",
                type = Executor.class,
                args = {MappedStatement.class, Object.class}),
        // 在数据库执行之前被调用 优先当前接口的其他方法被调用
        @Signature(
                type = StatementHandler.class,
                method = "prepare",
                args = {Connection.class, Integer.class}),
        // 该方法在perpare方法执行之后被执行 用于处理参数信息
        @Signature(type = StatementHandler.class, method = "parameterize", args = Statement.class),
        // 只有在全局设置defaultExecutorType=batch时 执行数据操作才会调用此方法
        @Signature(type = StatementHandler.class, method = "batch", args = Statement.class),
        // 执行update/delete/insert时调用改方法
        @Signature(type = StatementHandler.class, method = "update", args = Statement.class),
        // 执行select时调用
        @Signature(
                type = StatementHandler.class,
                method = "query",
                args = {Statement.class, ResultHandler.class}),
        @Signature(
                type = StatementHandler.class,
                method = "queryCursor",
                args = {Statement.class}),

        // 在执行存储过程处理中出参的时候被调用
        @Signature(
                type = ParameterHandler.class,
                method = "getParameterObject",
                args = {}),
        // 在所有数据库方法设置sql参数时被调用
        @Signature(
                type = ParameterHandler.class,
                method = "setParameters",
                args = {java.sql.PreparedStatement.class}),

        // 返回结果时被调用
        @Signature(
                type = ResultSetHandler.class,
                method = "handleResultSets",
                args = {Statement.class}),
        @Signature(
                type = ResultSetHandler.class,
                method = "handleCursorResultSets",
                args = {Statement.class})
})
public class SqlValidateInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 被代理对象
        Object target = invocation.getTarget();
        // 代理方法
        Method method = invocation.getMethod();
        // 方法参数
        Object[] args = invocation.getArgs();

        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        // sql语句
        Object parameter = null;
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }

        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        String sql = boundSql.getSql();

        LogUtils.info("校验sql -------------------------- start");
        validateSQL4(sql);
        LogUtils.info("校验sql -------------------------- success");

        return invocation.proceed();
    }

    public static boolean validateSQL4(String sql) throws SQLException {
        try {
            SqlParser.Config config = SqlParser.config();
            SqlParserImplFactory factory = config.parserFactory();
            SqlParser parser = SqlParser.create(sql, config.withParserFactory(factory));
            org.apache.calcite.sql.SqlNode node = parser.parseStmt();
            return true;
        } catch (SqlParseException e) {
            throw new SQLException("sql语法错误");
        }
    }

    public static boolean validateSQL3(String sql) throws SQLException {
        try {
            org.antlr.v4.runtime.CharStream input = CharStreams.fromString(sql);
            // SQLLexer lexer = new SQLLexer(input);
            // CommonTokenStream tokens = new CommonTokenStream(lexer);
            // SQLParser parser = new SQLParser(tokens);
            // ParseTree tree = parser.statement();
            return true;
        } catch (Exception e) {
            throw new SQLException("sql语法错误");
        }
    }

    // JSqlParser库只能检查SQL语句的语法是否合法，而无法检查SQL语句的语义是否合法。因此，同样需要进行严格的输入校验和过滤，避免SQL注入攻击。
    public static boolean validateSQL2(String sql) throws SQLException {
        try {
            net.sf.jsqlparser.statement.Statement stmt = CCJSqlParserUtil.parse(sql);
            return true;
        } catch (JSQLParserException e) {
            throw new SQLException("sql语法错误");
        }
    }

    // 方法只能判断SQL语句的语法是否合法，而无法判断SQL语句的语义是否合法。因此，如果应用程序允许用户输入SQL语句，一定要进行严格的输入校验和过滤，避免SQL注入攻击
    public static boolean validateSQL1(String sql) throws SQLException {
        try {
            Connection conn =
                    DriverManager.getConnection(
                            "jdbc:mysql://127.0.0.1:3306/mydatabase", "username", "password");
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            return true;
        } catch (SQLException e) {
            throw new SQLException("sql语法错误");
        }
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
