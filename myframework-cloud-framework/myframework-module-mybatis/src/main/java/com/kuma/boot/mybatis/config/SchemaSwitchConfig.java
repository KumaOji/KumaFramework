/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.mybatis.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * Schema 切换配置 - 独立配置类，避免 MybatisPlusConfig 被提前实例化
 *
 * @author kuma
 */
@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class SchemaSwitchConfig {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public SchemaSwitchInterceptor schemaSwitchInterceptor(MybatisPlusProperties properties) {
        SchemaSwitchInterceptor interceptor = new SchemaSwitchInterceptor();
        interceptor.setDefaultSchema(properties.getDefaultSchema());
        return interceptor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public static BeanPostProcessor schemaSwitchInterceptorBeanPostProcessor(
            ObjectProvider<SchemaSwitchInterceptor> interceptorProvider) {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof SqlSessionFactory factory) {
                    interceptorProvider.ifAvailable(factory.getConfiguration()::addInterceptor);
                }
                return bean;
            }
        };
    }
}
