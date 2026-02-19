/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.github.benmanes.caffeine.cache.CacheLoader
 *  com.github.benmanes.caffeine.cache.Caffeine
 *  com.github.benmanes.caffeine.cache.CaffeineSpec
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.jspecify.annotations.Nullable
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.ObjectProvider
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.cache.autoconfigure.CacheAutoConfiguration
 *  org.springframework.boot.cache.autoconfigure.CacheManagerCustomizer
 *  org.springframework.boot.cache.autoconfigure.CacheManagerCustomizers
 *  org.springframework.boot.cache.autoconfigure.CacheProperties
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.cache.CacheManager
 *  org.springframework.cache.annotation.EnableCaching
 *  org.springframework.cache.caffeine.CaffeineCacheManager
 *  org.springframework.context.annotation.Bean
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.cache.caffeine.autoconfigure;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import com.kuma.boot.cache.caffeine.autoconfigure.properties.CaffeineProperties;
import com.kuma.boot.cache.caffeine.manager.CaffeineAutoCacheManager;
import com.kuma.boot.cache.caffeine.repository.CaffeineRepository;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Collection;
import java.util.List;
import org.jspecify.annotations.Nullable;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@EnableCaching
@ConditionalOnClass(value={Caffeine.class, CaffeineCacheManager.class})
@AutoConfiguration(before={CacheAutoConfiguration.class})
@EnableConfigurationProperties(value={CaffeineProperties.class, CacheProperties.class})
@ConditionalOnProperty(prefix="kuma.boot.cache.caffeine", name={"enabled"}, havingValue="true")
public class CaffeineCacheAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(CaffeineCacheAutoConfiguration.class, (String)"kuma-boot-starter-cache-caffeine", (String[])new String[0]);
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
    @Bean(value={"caffeineCacheManager"})
    public CacheManager caffeineCacheManager(CacheProperties cacheProperties, CacheManagerCustomizers customizers, ObjectProvider<Caffeine<Object, Object>> caffeine, ObjectProvider<CaffeineSpec> caffeineSpec, ObjectProvider<CacheLoader<Object, Object>> cacheLoader) {
        CaffeineAutoCacheManager cacheManager = CaffeineCacheAutoConfiguration.createCacheManager(cacheProperties, caffeine, caffeineSpec, cacheLoader);
        List cacheNames = cacheProperties.getCacheNames();
        if (!CollectionUtils.isEmpty((Collection)cacheNames)) {
            cacheManager.setCacheNames(cacheNames);
        }
        return customizers.customize((CacheManager)cacheManager);
    }

    private static CaffeineAutoCacheManager createCacheManager(CacheProperties cacheProperties, ObjectProvider<Caffeine<Object, Object>> caffeine, ObjectProvider<CaffeineSpec> caffeineSpec, ObjectProvider<CacheLoader<Object, Object>> cacheLoader) {
        CaffeineAutoCacheManager cacheManager = new CaffeineAutoCacheManager();
        CaffeineCacheAutoConfiguration.setCacheBuilder(cacheProperties, (CaffeineSpec)caffeineSpec.getIfAvailable(), (Caffeine<Object, Object>)((Caffeine)caffeine.getIfAvailable()), cacheManager);
        cacheLoader.ifAvailable(arg_0 -> ((CaffeineAutoCacheManager)cacheManager).setCacheLoader(arg_0));
        return cacheManager;
    }

    private static void setCacheBuilder(CacheProperties cacheProperties, @Nullable CaffeineSpec caffeineSpec, @Nullable Caffeine<Object, Object> caffeine, CaffeineCacheManager cacheManager) {
        String specification = cacheProperties.getCaffeine().getSpec();
        if (StringUtils.hasText((String)specification)) {
            cacheManager.setCacheSpecification(specification);
        } else if (caffeineSpec != null) {
            cacheManager.setCaffeineSpec(caffeineSpec);
        } else if (caffeine != null) {
            cacheManager.setCaffeine(caffeine);
        }
    }
}

