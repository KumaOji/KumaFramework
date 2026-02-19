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

package com.kuma.boot.data.datasource.autoconfigure;

import com.baomidou.dynamic.datasource.processor.DsJakartaHeaderProcessor;
import com.baomidou.dynamic.datasource.processor.DsJakartaSessionProcessor;
import com.baomidou.dynamic.datasource.processor.DsProcessor;
import com.baomidou.dynamic.datasource.processor.DsSpelExpressionProcessor;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.datasource.dynamic.mybatisplus.config.DsLastParamProcessor;
import com.kuma.boot.data.datasource.dynamic.mybatisplus.config.JdbcDynamicDataSourceProvider;
import com.kuma.boot.data.datasource.dynamic.mybatisplus.properties.DynamicDataSourceProperties;
//import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;

/**
 * 动态数据源切换配置
 */
@AutoConfiguration
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
@ConditionalOnProperty(
        prefix = DynamicDataSourceProperties.PREFIX,
        name = "enabled",
        havingValue = "true")
public class DynamicDataSourceAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(DynamicDataSourceAutoConfiguration.class, StarterNameConstants.DATASOURCE_STARTER);
    }

    /**
     * 从数据库获取数据源信息
     */
    @Bean
    public DynamicDataSourceProvider dynamicDataSourceProvider(
//            StringEncryptor stringEncryptor,
            DataSourceProperties properties,
            DynamicDataSourceProperties dynamicDataSourceProperties) {
        return new JdbcDynamicDataSourceProvider(
//                stringEncryptor,
                properties, dynamicDataSourceProperties);
    }

    /**
     * 动态解析数据源
     */
    @Bean
    public DsProcessor dsProcessor() {
        DsLastParamProcessor lastParamProcessor = new DsLastParamProcessor();
        DsJakartaHeaderProcessor headerProcessor = new DsJakartaHeaderProcessor();
        DsJakartaSessionProcessor sessionProcessor = new DsJakartaSessionProcessor();
        DsSpelExpressionProcessor spelExpressionProcessor = new DsSpelExpressionProcessor();

        lastParamProcessor.setNextProcessor(headerProcessor);
        headerProcessor.setNextProcessor(sessionProcessor);
        sessionProcessor.setNextProcessor(spelExpressionProcessor);
        return lastParamProcessor;
    }
}
