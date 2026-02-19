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

package com.kuma.boot.data.mybatis.interceptor.log.configuration;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mybatis.interceptor.log.interceptor.ActualSqlInterceptor;
import com.kuma.boot.data.mybatis.interceptor.log.properties.ActualSqlProperties;
import java.util.List;
import java.util.Properties;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;

/**
 * SQL日志功能启动类
 * <p>
 * 这个是 logback-spring.xml 的写法，推荐使用这个区分环境
 * <springProfile name="dev,test,pre">
 * <logger name="com.kuma.boot.data.mybatis.sql" level="TRACE" />
 * </springProfile>
 */
@EnableConfigurationProperties(ActualSqlProperties.class)
@ConditionalOnClass(SqlSessionFactory.class)
@AutoConfigureAfter(MybatisAutoConfiguration.class)
@Configuration
@Lazy(false)
@Order
public class ActualSqlConfiguration implements InitializingBean {

    @Autowired ActualSqlProperties actualSqlProperties;

    @Autowired List<SqlSessionFactory> sqlSessionFactoryList;

    @Override
    public void afterPropertiesSet() throws Exception {
        ActualSqlInterceptor interceptor = new ActualSqlInterceptor();
        Properties properties = new Properties();
        properties.put("", actualSqlProperties);
        interceptor.setProperties(properties);
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            org.apache.ibatis.session.Configuration configuration =
                    sqlSessionFactory.getConfiguration();
            if (!isInterceptorExists(configuration, interceptor)) {
                configuration.addInterceptor(interceptor);
            }
        }
        printGraph();
    }

    /**
     * 是否已经存在指定的拦截器
     *
     * @param configuration mybatis的配置
     * @param interceptor   指定拦截器
     * @return 存在/不存在
     */
    private static boolean isInterceptorExists(
            org.apache.ibatis.session.Configuration configuration, Interceptor interceptor) {
        try {
            return configuration.getInterceptors().contains(interceptor);
        } catch (Exception e) {
            return false;
        }
    }

    private static void printGraph() {
        LogUtils.info("  _  ___   _ __  __    _         ____   ___  _          _     ___   ____ \n" +
                " | |/ / | | |  \\/  |  / \\       / ___| / _ \\| |        | |   / _ \\ / ___|\n" +
                " | ' /| | | | |\\/| | / _ \\      \\___ \\| | | | |        | |  | | | | |  _ \n" +
                " | . \\| |_| | |  | |/ ___ \\      ___) | |_| | |___     | |__| |_| | |_| |\n" +
                " |_|\\_\\\\___/|_|  |_/_/   \\_\\    |____/ \\__\\_\\_____|    |_____\\___/ \\____|\n");
    }
}
