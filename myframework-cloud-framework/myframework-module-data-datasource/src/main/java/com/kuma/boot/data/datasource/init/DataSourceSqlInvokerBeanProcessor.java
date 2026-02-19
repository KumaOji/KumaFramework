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

package com.kuma.boot.data.datasource.init;

import com.kuma.boot.core.enums.KmcEnvEnum;
import com.kuma.boot.core.holder.RuntimeContextHolder;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;

/**
 * DataSourceSqlInvokerBeanProcessor
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class DataSourceSqlInvokerBeanProcessor implements BeanPostProcessor, Ordered {

    public static final String PRIMARY_DATASOURCE_BEAN_NAME = "dataSource";

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInitialization( Object bean, String beanName )
            throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization( Object bean, String beanName )
            throws BeansException {
        if (bean instanceof DataSource
                && isPrimaryDataSource((DataSource) bean, beanName)
                && ( KmcEnvEnum.getCurrEnv() != KmcEnvEnum.PRE
                || KmcEnvEnum.getCurrEnv() != KmcEnvEnum.PRO )) {

            if (!RuntimeContextHolder.getInstance().isTestEnvironment()) {
                this.applicationContext
                        .getBeansOfType(StandardDatabaseScript.class)
                        .forEach(
                                ( k, v ) -> {
                                    v.setDataSource((DataSource) bean);
                                    v.execute();
                                });
            }
        }
        return bean;
    }

    private boolean isPrimaryDataSource( DataSource dataSource, String beanName ) {
        return PRIMARY_DATASOURCE_BEAN_NAME.equals(beanName);
        // && dataSource instanceof DruidDataSourceWrapper;
    }
}
