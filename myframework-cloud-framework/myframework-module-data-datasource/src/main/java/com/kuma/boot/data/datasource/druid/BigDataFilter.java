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

package com.kuma.boot.data.datasource.druid;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.kuma.boot.common.utils.log.LogUtils;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;

/**
 * 想要限制每次查询的结果集不能超过10000行，该如何实现？
 *
 */
public class BigDataFilter extends FilterEventAdapter {

    @Override
    public ResultSetProxy preparedStatement_executeQuery(
            FilterChain chain, PreparedStatementProxy statement) throws SQLException {
        ResultSetProxy resultSetProxy = super.preparedStatement_executeQuery(chain, statement);

        ResultSetProxy proxyInstance =
                (ResultSetProxy)
                        Proxy.newProxyInstance(
                                FilterEventAdapter.class.getClassLoader(),
                                new Class[] {ResultSetProxy.class},
                                new InvocationHandler() {
                                    int count = 0;

                                    @Override
                                    public Object invoke(Object proxy, Method method, Object[] args)
                                            throws Throwable {
                                        Object result = method.invoke(resultSetProxy, args);
                                        if ("next".equals(method.getName())) {
                                            if (result.equals(true)) {
                                                count++;

                                                LogUtils.info("累积获取数据");
                                                if (count >= 1000) {
                                                    LogUtils.info("超过了限制");
                                                }
                                            }
                                        }
                                        return result;
                                    }
                                });
        return proxyInstance;
    }
}
