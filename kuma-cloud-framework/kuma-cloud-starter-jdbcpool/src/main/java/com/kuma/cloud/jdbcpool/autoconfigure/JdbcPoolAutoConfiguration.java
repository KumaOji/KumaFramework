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

package com.kuma.cloud.jdbcpool.autoconfigure;

import com.kuma.cloud.jdbcpool.bs.JdbcPoolBs;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * JdbcPool Spring Boot 自动配置
 *
 * @author kuma
 * @since 1.8.0
 */
@AutoConfiguration
@EnableConfigurationProperties(JdbcPoolProperties.class)
public class JdbcPoolAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource dataSource(JdbcPoolProperties properties) {
        return JdbcPoolBs.newInstance()
                .driverClass(properties.getDriverClass())
                .url(properties.getUrl())
                .username(properties.getUsername())
                .password(properties.getPassword())
                .minSize(properties.getMinSize())
                .maxSize(properties.getMaxSize())
                .maxWaitMills(properties.getMaxWaitMills())
                .validQuery(properties.getValidQuery())
                .validTimeOutSeconds(properties.getValidTimeOutSeconds())
                .testOnBorrow(properties.isTestOnBorrow())
                .testOnReturn(properties.isTestOnReturn())
                .testOnIdle(properties.isTestOnIdle())
                .testOnIdleIntervalSeconds(properties.getTestOnIdleIntervalSeconds())
                .pooled();
    }
}
