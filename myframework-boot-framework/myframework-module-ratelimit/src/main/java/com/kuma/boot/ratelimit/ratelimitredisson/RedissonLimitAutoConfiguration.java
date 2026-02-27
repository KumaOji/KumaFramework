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

package com.kuma.boot.ratelimit.ratelimitredisson;

import com.kuma.boot.ratelimit.ratelimitredisson.executor.RedissonLimitExecutor;
import com.kuma.boot.ratelimit.ratelimitredisson.interceptor.LimitInterceptor;
import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Role;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * The type Limit auto configuration.
 *
 */
@AutoConfiguration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class RedissonLimitAutoConfiguration {

    /**
     * Limit interceptor limit interceptor.
     *
     * @param provider the provider
     * @return the limit interceptor
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LimitInterceptor limitInterceptor(ObjectProvider<LimitExecutor> provider) {
        return new LimitInterceptor(provider);
    }

    /**
     * The type Redisson limit configuration.
     */
    @ConditionalOnClass(RedissonClient.class)
    @AutoConfiguration(
            after = {RedissonAutoConfigurationV2.class},
            afterName = {"org.redisson.spring.starter.RedissonAutoConfigurationV2"})
    public static class RedissonLimitConfiguration {
        /**
         * Redisson limit executor limit executor.
         *
         * @param redissonClient the redisson client
         * @return the limit executor
         */
        @Bean
        @ConditionalOnMissingBean
        public LimitExecutor redissonLimitExecutor(RedissonClient redissonClient) {
            return new RedissonLimitExecutor(redissonClient);
        }
    }
}
