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

package com.kuma.boot.data.mybatis.interceptor;

import java.util.Properties;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * Executor
 * update：该方法会在所有的 INSERT、 UPDATE、 DELETE 执行时被调用，如果想要拦截这些操作，可以通过该方法实现。
 * query：该方法会在 SELECT 查询方法执行时被调用，方法参数携带了很多有用的信息，如果需要获取，可以通过该方法实现。
 * queryCursor：当 SELECT 的返回类型是 Cursor 时，该方法会被调用。
 * flushStatements：当 SqlSession 方法调用 flushStatements 方法或执行的接口方法中带有 @Flush 注解时该方法会被触发。
 * commit：当 SqlSession 方法调用 commit 方法时该方法会被触发。
 * rollback：当 SqlSession 方法调用 rollback 方法时该方法会被触发。
 * getTransaction：当 SqlSession 方法获取数据库连接时该方法会被触发。
 * close：该方法在懒加载获取新的 Executor 后会被触发。
 * isClosed：该方法在懒加载执行查询前会被触发。
 *
 * ParameterHandler
 * getParameterObject：在执行存储过程处理出参的时候该方法会被触发。
 * setParameters：设置 SQL 参数时该方法会被触发。
 *
 * ResultSetHandler
 * handleResultSets：该方法会在所有的查询方法中被触发（除去返回值类型为 Cursor的查询方法），一般来说，如果我们想对查询结果进行二次处理，可以通过拦截该方法实现。
 * handleCursorResultSets：当查询方法的返回值类型为 Cursor时，该方法会被触发。
 * handleOutputParameters：使用存储过程处理出参的时候该方法会被调用。
 *
 * StatementHandler
 * prepare：该方法在数据库执行前被触发。
 * parameterize：该方法在 prepare 方法之后执行，用来处理参数信息。
 * batch：如果 MyBatis 的全剧配置中配置了 defaultExecutorType=”BATCH”，执行数据操作时该方法会被调用。
 * update：更新操作时该方法会被触发。
 * query：该方法在 SELECT 方法执行时会被触发。
 * queryCursor：该方法在 SELECT 方法执行时，并且返回值为 Cursor 时会被触发。
 *
 */
@Intercepts({
        // 所有的query调用的执行
        @Signature(
                method = "query",
                type = Executor.class,
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
})
public class TestInterceptor implements Interceptor {

    // 这个就是具体的拦截方法，我们自定义 MyBatis 插件时，
    // 一般都需要重写该方法，我们插件所完成的工作也都是在该方法中完成的。
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        return invocation.proceed();
    }

    // 这个方法的参数 target 就是拦截器要拦截的对象，一般来说我们不需要重写该方法。Plugin.wrap
    // 方法会自动判断拦截器的签名和被拦截对象的接口是否匹配，如果匹配，才会通过动态代理拦截目标对象。
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    // 这个方法用来传递插件的参数，可以通过参数来改变插件的行为。我们定义好插件之后，需要对插件进行配置，
    // 在配置的时候，可以给插件设置相关属性，设置的属性可以通过该方法获取到。插件属性设置像下面这样
    @Override
    public void setProperties(Properties properties) {}
}
