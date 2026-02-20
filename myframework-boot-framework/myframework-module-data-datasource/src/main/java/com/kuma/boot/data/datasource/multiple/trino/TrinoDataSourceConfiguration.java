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

package com.kuma.boot.data.datasource.multiple.trino;

import com.alibaba.druid.pool.DruidDataSource;
import com.kuma.boot.data.datasource.multiple.autoconfigure.properties.MultipleDataSourceCommonProperties;
import com.kuma.boot.data.datasource.multiple.autoconfigure.properties.MultipleDataSourceProperties;

import java.sql.SQLException;
import java.util.TimeZone;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;

/**
 * TrinoDataSourceConfiguration
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
@AutoConfiguration
@ConditionalOnClass(name = "io.trino.jdbc.TrinoDriver")
@ConditionalOnProperty(name = "kuma.boot.data.datasource.multiple.trino.enabled", havingValue = "true")
@Import(TrinoJdbcBaseDaoImpl.class)
public class TrinoDataSourceConfiguration {

    private static Logger logger = LoggerFactory.getLogger(TrinoDataSourceConfiguration.class);

    private final MultipleDataSourceProperties extDataSourceProperties;
    private final MultipleDataSourceCommonProperties extDataSourceCommonProperties;

    public TrinoDataSourceConfiguration(
            MultipleDataSourceProperties extDataSourceProperties,
            MultipleDataSourceCommonProperties extDataSourceCommonProperties ) {
        this.extDataSourceProperties = extDataSourceProperties;
        this.extDataSourceCommonProperties = extDataSourceCommonProperties;
    }

    @Bean("trinoDruidDataSource") // 新建bean实例
    @Qualifier("trinoDruidDataSource") // 标识
    public DataSource trinoDruidDataSource() {
        TimeZone.setDefault(TimeZone.getTimeZone("+08:00"));
        DruidDataSource datasource = new DruidDataSource();

        // 配置数据源属性
        datasource.setUrl(extDataSourceProperties.getTrino().getUrl());
        datasource.setUsername(extDataSourceProperties.getTrino().getUsername());
        datasource.setPassword(extDataSourceProperties.getTrino().getPassword());
        datasource.setDriverClassName(extDataSourceProperties.getTrino().getDriverClassName());

        // 配置统一属性
        datasource.setInitialSize(extDataSourceCommonProperties.getInitialSize());
        datasource.setMinIdle(extDataSourceCommonProperties.getMinIdle());
        datasource.setMaxActive(extDataSourceCommonProperties.getMaxActive());
        datasource.setMaxWait(extDataSourceCommonProperties.getMaxWait());
        datasource.setTimeBetweenEvictionRunsMillis(
                extDataSourceCommonProperties.getTimeBetweenEvictionRunsMillis());
        datasource.setMinEvictableIdleTimeMillis(
                extDataSourceCommonProperties.getMinEvictableIdleTimeMillis());
        datasource.setValidationQuery(extDataSourceCommonProperties.getValidationQuery());
        datasource.setTestWhileIdle(extDataSourceCommonProperties.isTestWhileIdle());
        datasource.setTestOnBorrow(extDataSourceCommonProperties.isTestOnBorrow());
        datasource.setTestOnReturn(extDataSourceCommonProperties.isTestOnReturn());
        datasource.setPoolPreparedStatements(
                extDataSourceCommonProperties.isPoolPreparedStatements());
        try {
            datasource.setFilters(extDataSourceCommonProperties.getFilters());
        } catch (SQLException e) {
            logger.error("Druid configuration initialization filter error.", e);
        }
        return datasource;
    }

    @Bean(name = "trinoJdbcTemplate")
    public JdbcTemplate trinoJdbcTemplate(
            @Qualifier("trinoDruidDataSource") DataSource dataSource ) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "trinoJdbcClient")
    public JdbcClient trinoJdbcClient( @Qualifier("trinoDruidDataSource") DataSource dataSource ) {
        return JdbcClient.create(dataSource);
    }
}
