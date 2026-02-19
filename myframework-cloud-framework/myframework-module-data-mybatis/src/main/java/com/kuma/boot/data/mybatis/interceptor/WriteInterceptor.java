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

import com.kuma.boot.data.datasource.dynamic.spring.DataSourceContextHolder;
import com.kuma.boot.data.datasource.dynamic.spring.DataSourceTypeEnum;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.stereotype.Component;

// 集成 mybatis 并在写入时强制切换到主库
// 不需要做任何配置，正常集成 mybatis 即可使用读写分离功能
//
// 可以通过 mybatis 的拦截器在写入操作时强制切换到主库
/**
 * WriteInterceptor
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
@Intercepts({
        @Signature(
                type = Executor.class,
                method = "update",
                args = {MappedStatement.class, Object.class}),
})
@Component
public class WriteInterceptor implements Interceptor {

    @Override
    public Object intercept( Invocation invocation ) throws Throwable {
        // 获取 SQL 类型
        DataSourceTypeEnum dataSourceType = DataSourceContextHolder.getDataSourceType();
        if (DataSourceTypeEnum.SLAVE.equals(dataSourceType)) {
            DataSourceContextHolder.setDataSourceType(DataSourceTypeEnum.MASTER);
        }
        try {
            // 执行 SQL
            return invocation.proceed();
        } finally {
            // 恢复数据源  考虑到写入后可能会反查，后续都走主库
            // DataSourceContextHolder.setDataSourceType(dataSourceType);
        }
    }
}
