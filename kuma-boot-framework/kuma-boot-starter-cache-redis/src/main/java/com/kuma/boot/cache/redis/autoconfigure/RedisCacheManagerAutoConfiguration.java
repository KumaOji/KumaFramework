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

package com.kuma.boot.cache.redis.autoconfigure;

import com.kuma.boot.cache.redis.autoconfigure.properties.CacheManagerProperties;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.constant.StrPoolConstants;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.cache.autoconfigure.CacheManagerCustomizer;
import org.springframework.boot.cache.autoconfigure.CacheManagerCustomizers;
import org.springframework.boot.cache.autoconfigure.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Role;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.jspecify.annotations.Nullable;

/**
 * redis cache manager自动配置类
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-07 21:17:09
 */
@EnableCaching
@AutoConfiguration(after = {RedisAutoConfiguration.class})
@EnableConfigurationProperties({CacheProperties.class})
@ConditionalOnBean(RedisSerializer.class)
@ConditionalOnProperty(
        prefix = CacheManagerProperties.PREFIX,
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class RedisCacheManagerAutoConfiguration implements CachingConfigurer, InitializingBean {

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(RedisCacheManagerAutoConfiguration.class, StarterNameConstants.CACHE_REDIS_STARTER);
    }

    private final CacheProperties cacheProperties;
    /**
     * 序列化方式
     */
    private final RedisSerializer<Object> redisSerializer;

    @Nullable
    private final RedisCacheConfiguration redisCacheConfiguration;

    RedisCacheManagerAutoConfiguration(
            RedisSerializer<Object> redisSerializer,
            CacheProperties cacheProperties,
            ObjectProvider<RedisCacheConfiguration> redisCacheConfiguration) {
        this.redisSerializer = redisSerializer;
        this.cacheProperties = cacheProperties;
        this.redisCacheConfiguration = redisCacheConfiguration.getIfAvailable();
    }

    @Bean
    public CacheManagerCustomizers cacheManagerCustomizers(
            ObjectProvider<List<CacheManagerCustomizer<?>>> customizers) {
        return new CacheManagerCustomizers(customizers.getIfAvailable());
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, objects) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(StrPoolConstants.COLON);
            sb.append(method.getName());
            for (Object obj : objects) {
                if (obj != null) {
                    sb.append(StrPoolConstants.COLON);
                    sb.append(obj);
                }
            }
            return sb.toString();
        };
    }

    /**
     * 自定义缓存异常处理 当缓存读写异常时，忽略异常
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object o) {
                LogUtils.error(e.getMessage(), e);
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object o, Object o1) {
                LogUtils.error(e.getMessage(), e);
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object o) {
                LogUtils.error(e.getMessage(), e);
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                LogUtils.error(e.getMessage(), e);
            }
        };
    }

    @Primary
    @Bean
    @ConditionalOnBean(RedissonConnectionFactory.class)
    @ConditionalOnProperty(
            prefix = CacheManagerProperties.PREFIX,
            name = "type",
            havingValue = "redis",
            matchIfMissing = true)
    public CacheManager redisCacheManager(
            CacheManagerCustomizers cacheManagerCustomizers,
            ObjectProvider<RedissonConnectionFactory> connectionFactoryObjectProvider) {
        RedisConnectionFactory connectionFactory = connectionFactoryObjectProvider.getIfAvailable();
        Objects.requireNonNull(connectionFactory, "Bean RedisConnectionFactory is null.");
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
        RedisCacheConfiguration cacheConfiguration = this.determineConfiguration();
        List<String> cacheNames = this.cacheProperties.getCacheNames();
        Map<String, RedisCacheConfiguration> initialCaches = new LinkedHashMap<>();
        if (!cacheNames.isEmpty()) {
            Map<String, RedisCacheConfiguration> cacheConfigMap = new LinkedHashMap<>(cacheNames.size());
            cacheNames.forEach(it -> cacheConfigMap.put(it, cacheConfiguration));
            initialCaches.putAll(cacheConfigMap);
        }

        RedisAutoCacheManager cacheManager = new RedisAutoCacheManager(
                redisCacheWriter, cacheConfiguration, true, initialCaches);
        cacheManager.setTransactionAware(false);

        return cacheManagerCustomizers.customize(cacheManager);
    }

    private RedisCacheConfiguration determineConfiguration() {
        if (this.redisCacheConfiguration != null) {
            return this.redisCacheConfiguration;
        } else {
            CacheProperties.Redis redisProperties =
                    this.cacheProperties.getRedis();
            RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
            config = config.serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer));
            if (redisProperties.getTimeToLive() != null) {
                config = config.entryTtl(redisProperties.getTimeToLive());
            }

            if (redisProperties.getKeyPrefix() != null) {
                config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
            }

            if (!redisProperties.isCacheNullValues()) {
                config = config.disableCachingNullValues();
            }

            if (!redisProperties.isUseKeyPrefix()) {
                config = config.disableKeyPrefix();
            }

            return config;
        }
    }

    /**
     * redis cache 扩展cache name自动化配置
     */
    public static class RedisAutoCacheManager extends RedisCacheManager {

        public RedisAutoCacheManager(
                RedisCacheWriter cacheWriter,
                RedisCacheConfiguration defaultCacheConfiguration,
                boolean allowRuntimeCacheCreation,
                Map<String, RedisCacheConfiguration> initialCacheConfigurations) {
            super(cacheWriter, defaultCacheConfiguration, allowRuntimeCacheCreation, initialCacheConfigurations);
        }

        @Override
        protected RedisCache createRedisCache(String name, @Nullable RedisCacheConfiguration cacheConfig) {
            if (StringUtils.isBlank(name) || !name.contains(StrPoolConstants.HASH)) {
                return super.createRedisCache(name, cacheConfig);
            }
            String[] cacheArray = name.split(StrPoolConstants.HASH);
            if (cacheArray.length < 2) {
                return super.createRedisCache(name, cacheConfig);
            }
            String cacheName = cacheArray[0];
            if (cacheConfig != null) {
                // 转换时间，支持时间单位例如：300ms，第二个参数是默认单位
                Duration duration = DurationStyle.detectAndParse(cacheArray[1], ChronoUnit.SECONDS);
                cacheConfig = cacheConfig.entryTtl(duration);
            }
            return super.createRedisCache(cacheName, cacheConfig);
        }
    }

}
