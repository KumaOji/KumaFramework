/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.jspecify.annotations.Nullable
 *  org.redisson.spring.data.connection.RedissonConnectionFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.ObjectProvider
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.cache.autoconfigure.CacheManagerCustomizer
 *  org.springframework.boot.cache.autoconfigure.CacheManagerCustomizers
 *  org.springframework.boot.cache.autoconfigure.CacheProperties
 *  org.springframework.boot.cache.autoconfigure.CacheProperties$Redis
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.boot.convert.DurationStyle
 *  org.springframework.cache.Cache
 *  org.springframework.cache.CacheManager
 *  org.springframework.cache.annotation.CachingConfigurer
 *  org.springframework.cache.annotation.EnableCaching
 *  org.springframework.cache.interceptor.CacheErrorHandler
 *  org.springframework.cache.interceptor.KeyGenerator
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Primary
 *  org.springframework.context.annotation.Role
 *  org.springframework.data.redis.cache.RedisCache
 *  org.springframework.data.redis.cache.RedisCacheConfiguration
 *  org.springframework.data.redis.cache.RedisCacheManager
 *  org.springframework.data.redis.cache.RedisCacheWriter
 *  org.springframework.data.redis.connection.RedisConnectionFactory
 *  org.springframework.data.redis.serializer.RedisSerializationContext$SerializationPair
 *  org.springframework.data.redis.serializer.RedisSerializer
 */
package com.kuma.boot.cache.redis.autoconfigure;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
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

@EnableCaching
@AutoConfiguration(after={RedisAutoConfiguration.class})
@EnableConfigurationProperties(value={CacheProperties.class})
@ConditionalOnProperty(prefix="kuma.boot.cache.redis.cache-manager", name={"enabled"}, havingValue="true", matchIfMissing=true)
@Role(value=2)
public class RedisCacheManagerAutoConfiguration
implements CachingConfigurer,
InitializingBean {
    private final CacheProperties cacheProperties;
    private final RedisSerializer<Object> redisSerializer;
    private final @Nullable RedisCacheConfiguration redisCacheConfiguration;

    public void afterPropertiesSet() throws Exception {
        LogUtils.started(RedisCacheManagerAutoConfiguration.class, (String)"kuma-boot-starter-cache-redis", (String[])new String[0]);
    }

    RedisCacheManagerAutoConfiguration(RedisSerializer<Object> redisSerializer, CacheProperties cacheProperties, ObjectProvider<RedisCacheConfiguration> redisCacheConfiguration) {
        this.redisSerializer = redisSerializer;
        this.cacheProperties = cacheProperties;
        this.redisCacheConfiguration = (RedisCacheConfiguration)redisCacheConfiguration.getIfAvailable();
    }

    @Bean
    public CacheManagerCustomizers cacheManagerCustomizers(ObjectProvider<List<CacheManagerCustomizer<?>>> customizers) {
        return new CacheManagerCustomizers((List)customizers.getIfAvailable());
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, objects) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(":");
            sb.append(method.getName());
            for (Object obj : objects) {
                if (obj == null) continue;
                sb.append(":");
                sb.append(obj);
            }
            return sb.toString();
        };
    }

    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler(this){
            {
                Objects.requireNonNull(this$0);
            }

            public void handleCacheGetError(RuntimeException e, Cache cache, Object o) {
                LogUtils.error((String)e.getMessage(), (Object[])new Object[]{e});
            }

            public void handleCachePutError(RuntimeException e, Cache cache, Object o, Object o1) {
                LogUtils.error((String)e.getMessage(), (Object[])new Object[]{e});
            }

            public void handleCacheEvictError(RuntimeException e, Cache cache, Object o) {
                LogUtils.error((String)e.getMessage(), (Object[])new Object[]{e});
            }

            public void handleCacheClearError(RuntimeException e, Cache cache) {
                LogUtils.error((String)e.getMessage(), (Object[])new Object[]{e});
            }
        };
    }

    @Primary
    @Bean
    @ConditionalOnBean(value={RedissonConnectionFactory.class})
    @ConditionalOnProperty(prefix="kuma.boot.cache.redis.cache-manager", name={"type"}, havingValue="redis", matchIfMissing=true)
    public CacheManager redisCacheManager(CacheManagerCustomizers cacheManagerCustomizers, ObjectProvider<RedissonConnectionFactory> connectionFactoryObjectProvider) {
        RedisConnectionFactory connectionFactory = (RedisConnectionFactory)connectionFactoryObjectProvider.getIfAvailable();
        Objects.requireNonNull(connectionFactory, "Bean RedisConnectionFactory is null.");
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter((RedisConnectionFactory)connectionFactory);
        RedisCacheConfiguration cacheConfiguration = this.determineConfiguration();
        List cacheNames = this.cacheProperties.getCacheNames();
        LinkedHashMap<String, RedisCacheConfiguration> initialCaches = new LinkedHashMap<String, RedisCacheConfiguration>();
        if (!cacheNames.isEmpty()) {
            LinkedHashMap cacheConfigMap = new LinkedHashMap(cacheNames.size());
            cacheNames.forEach(it -> cacheConfigMap.put(it, cacheConfiguration));
            initialCaches.putAll(cacheConfigMap);
        }
        boolean allowInFlightCacheCreation = true;
        boolean enableTransactions = false;
        RedisAutoCacheManager cacheManager = new RedisAutoCacheManager(redisCacheWriter, cacheConfiguration, allowInFlightCacheCreation, initialCaches);
        cacheManager.setTransactionAware(enableTransactions);
        return cacheManagerCustomizers.customize((CacheManager)cacheManager);
    }

    private RedisCacheConfiguration determineConfiguration() {
        if (this.redisCacheConfiguration != null) {
            return this.redisCacheConfiguration;
        }
        CacheProperties.Redis redisProperties = this.cacheProperties.getRedis();
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(this.redisSerializer));
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

    public static class RedisAutoCacheManager
    extends RedisCacheManager {
        public RedisAutoCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, boolean allowRuntimeCacheCreation, Map<String, RedisCacheConfiguration> initialCacheConfigurations) {
            super(cacheWriter, defaultCacheConfiguration, allowRuntimeCacheCreation, initialCacheConfigurations);
        }

        protected RedisCache createRedisCache(String name, @Nullable RedisCacheConfiguration cacheConfig) {
            if (StringUtils.isBlank((String)name) || !name.contains("#")) {
                return super.createRedisCache(name, cacheConfig);
            }
            String[] cacheArray = name.split("#");
            if (cacheArray.length < 2) {
                return super.createRedisCache(name, cacheConfig);
            }
            String cacheName = cacheArray[0];
            if (cacheConfig != null) {
                Duration duration = DurationStyle.detectAndParse((String)cacheArray[1], (ChronoUnit)ChronoUnit.SECONDS);
                cacheConfig = cacheConfig.entryTtl(duration);
            }
            return super.createRedisCache(cacheName, cacheConfig);
        }
    }
}

