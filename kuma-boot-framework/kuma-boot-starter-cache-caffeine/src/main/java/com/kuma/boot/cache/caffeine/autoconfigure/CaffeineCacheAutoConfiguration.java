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

package com.kuma.boot.cache.caffeine.autoconfigure;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import com.kuma.boot.cache.caffeine.manager.CaffeineAutoCacheManager;
import com.kuma.boot.cache.caffeine.autoconfigure.properties.CaffeineProperties;
import com.kuma.boot.cache.caffeine.repository.CaffeineRepository;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.cache.autoconfigure.CacheAutoConfiguration;
import org.springframework.boot.cache.autoconfigure.CacheManagerCustomizer;
import org.springframework.boot.cache.autoconfigure.CacheManagerCustomizers;
import org.springframework.boot.cache.autoconfigure.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.jspecify.annotations.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Caffeine auto cache configuration.
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-03 09:33:45
 */
@EnableCaching
@ConditionalOnClass({Caffeine.class, CaffeineCacheManager.class})
@AutoConfiguration(before = CacheAutoConfiguration.class)
@EnableConfigurationProperties({CaffeineProperties.class, CacheProperties.class})
@ConditionalOnProperty(prefix = CaffeineProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class CaffeineCacheAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(CaffeineCacheAutoConfiguration.class, StarterNameConstants.CACHE_CAFFEINE_STARTER);
    }

    @Bean
    public CaffeineRepository caffeineRepository() {
        return new CaffeineRepository();
    }

    @Bean
    @ConditionalOnMissingBean
    public CacheManagerCustomizers cacheManagerCustomizers(ObjectProvider<CacheManagerCustomizer<?>> customizers) {
        return new CacheManagerCustomizers(customizers.orderedStream().toList());
    }

    @ConditionalOnMissingBean
    @Bean("caffeineCacheManager")
    public CacheManager caffeineCacheManager(
            CacheProperties cacheProperties,
            CacheManagerCustomizers customizers,
            ObjectProvider<Caffeine<Object, Object>> caffeine,
            ObjectProvider<CaffeineSpec> caffeineSpec,
            ObjectProvider<CacheLoader<Object, Object>> cacheLoader) {
        CaffeineAutoCacheManager cacheManager =
                createCacheManager(cacheProperties, caffeine, caffeineSpec, cacheLoader);
        List<String> cacheNames = cacheProperties.getCacheNames();
        if (!CollectionUtils.isEmpty(cacheNames)) {
            cacheManager.setCacheNames(cacheNames);
        }
        return customizers.customize(cacheManager);
    }

    private static CaffeineAutoCacheManager createCacheManager(
            CacheProperties cacheProperties,
            ObjectProvider<Caffeine<Object, Object>> caffeine,
            ObjectProvider<CaffeineSpec> caffeineSpec,
            ObjectProvider<CacheLoader<Object, Object>> cacheLoader) {
        CaffeineAutoCacheManager cacheManager = new CaffeineAutoCacheManager();
        setCacheBuilder(cacheProperties, caffeineSpec.getIfAvailable(), caffeine.getIfAvailable(), cacheManager);
        cacheLoader.ifAvailable(cacheManager::setCacheLoader);
        return cacheManager;
    }

    private static void setCacheBuilder(
            CacheProperties cacheProperties,
            @Nullable CaffeineSpec caffeineSpec,
            @Nullable Caffeine<Object, Object> caffeine,
            CaffeineCacheManager cacheManager) {
        String specification = cacheProperties.getCaffeine().getSpec();
        if (StringUtils.hasText(specification)) {
            cacheManager.setCacheSpecification(specification);
        } else if (caffeineSpec != null) {
            cacheManager.setCaffeineSpec(caffeineSpec);
        } else if (caffeine != null) {
            cacheManager.setCaffeine(caffeine);
        }
    }
}
