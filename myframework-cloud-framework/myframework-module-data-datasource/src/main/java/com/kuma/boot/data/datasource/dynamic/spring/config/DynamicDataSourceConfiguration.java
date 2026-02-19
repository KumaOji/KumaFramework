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

package com.kuma.boot.data.datasource.dynamic.spring.config;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

import com.kuma.boot.data.datasource.dynamic.spring.DataSourceTypeEnum;
import com.kuma.boot.data.datasource.dynamic.spring.DynamicRoutingDataSource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 排除掉数据源自动配置类 如果不排除自动配置类会导致初始化多个 dataSource 对象导致出现问题
 * <p>
 * SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
 */
@AutoConfiguration(after = {MasterDataSourceConfiguration.class,
        SlaveDataSourceConfiguration.class})
@ConditionalOnProperty("spring.datasource.master.jdbc-url")
public class DynamicDataSourceConfiguration {

    @Bean("dataSource")
    @Primary
    public DataSource dynamicDataSource(DataSource masterDataSource, DataSource slaveDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceTypeEnum.MASTER, masterDataSource);
        targetDataSources.put(DataSourceTypeEnum.SLAVE, slaveDataSource);

        DynamicRoutingDataSource dynamicDataSource = new DynamicRoutingDataSource();
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(masterDataSource);
        return dynamicDataSource;
    }
}
