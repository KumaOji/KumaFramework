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

package com.kuma.boot.cache.redis.delay.config;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.context.annotation.Scope;

/**
 * redis 延迟队列自动配置类
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-02-18 10:36:41
 */
@AutoConfiguration
@ConditionalOnBean({RedissonClient.class})
public class RedissonDelayAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(RedissonDelayAutoConfiguration.class, StarterNameConstants.CACHE_REDIS_STARTER);
    }

    @Scope(BeanDefinition.SCOPE_SINGLETON)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean(name = RedissonConfigUtils.REDISSON_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME)
    public RedissonAnnotationBeanPostProcessor redissonAnnotationBeanPostProcessor() {
        return new RedissonAnnotationBeanPostProcessor();
    }

    @Scope(BeanDefinition.SCOPE_SINGLETON)
    @Bean(name = RedissonConfigUtils.REDISSON_LISTENER_REGISTRY_BEAN_NAME)
    public RedissonListenerRegistry redissonListenerRegistry() {
        return new RedissonListenerRegistry();
    }

    @Scope(BeanDefinition.SCOPE_SINGLETON)
    @Bean(name = RedissonConfigUtils.REDISSON_QUEUE_BEAN_PROCESSOR_BEAN_NAME)
    public RedissonQueueBeanPostProcessor redissonQueueBeanPostProcessor() {
        return new RedissonQueueBeanPostProcessor();
    }

    @Scope(BeanDefinition.SCOPE_SINGLETON)
    @Bean(name = RedissonConfigUtils.REDISSON_QUEUE_REGISTRY_BEAN_NAME)
    public RedissonQueueRegistry redissonQueueRegistry() {
        return new RedissonQueueRegistry();
    }

    @Scope(BeanDefinition.SCOPE_SINGLETON)
    @Bean
    @ConditionalOnMissingBean
    public RedissonTemplate redissonTemplate() {
        return new RedissonTemplate();
    }
}
