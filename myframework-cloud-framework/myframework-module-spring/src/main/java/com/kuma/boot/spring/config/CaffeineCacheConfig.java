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

package com.kuma.boot.spring.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine 本地缓存配置
 * <p>
 * 配置项：spring.cache.caffeine.spec 或 kuma.cache.caffeine.*
 *
 * @author kuma
 */
@Configuration
@EnableCaching
@ConditionalOnClass(Caffeine.class)
@EnableConfigurationProperties(CaffeineCacheProperties.class)
public class CaffeineCacheConfig {

    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    public CacheManager cacheManager(CaffeineCacheProperties properties) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(properties.getCacheNames());
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(properties.getInitialCapacity())
                .maximumSize(properties.getMaximumSize())
                .expireAfterWrite(properties.getExpireAfterWriteMinutes(), TimeUnit.MINUTES)
                .expireAfterAccess(properties.getExpireAfterAccessMinutes(), TimeUnit.MINUTES)
                .recordStats());
        return cacheManager;
    }
}
