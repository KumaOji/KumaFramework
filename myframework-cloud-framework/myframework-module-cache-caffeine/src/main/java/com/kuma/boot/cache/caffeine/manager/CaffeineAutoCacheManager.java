/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.github.benmanes.caffeine.cache.Cache
 *  com.github.benmanes.caffeine.cache.CacheLoader
 *  com.github.benmanes.caffeine.cache.Caffeine
 *  com.github.benmanes.caffeine.cache.CaffeineSpec
 *  org.jspecify.annotations.Nullable
 *  org.springframework.boot.convert.DurationStyle
 *  org.springframework.cache.caffeine.CaffeineCacheManager
 *  org.springframework.util.ReflectionUtils
 */
package com.kuma.boot.cache.caffeine.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import java.lang.reflect.Field;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.util.ReflectionUtils;

public class CaffeineAutoCacheManager
extends CaffeineCacheManager {
    private static final Field CACHE_LOADER_FIELD = Objects.requireNonNull(ReflectionUtils.findField(CaffeineCacheManager.class, (String)"cacheLoader"));
    private @Nullable CaffeineSpec caffeineSpec = null;

    public CaffeineAutoCacheManager() {
    }

    public CaffeineAutoCacheManager(String ... cacheNames) {
        super(cacheNames);
    }

    protected @Nullable CacheLoader<Object, Object> getCacheLoader() {
        return (CacheLoader)ReflectionUtils.getField((Field)CACHE_LOADER_FIELD, (Object)((Object)this));
    }

    public void setCaffeine(Caffeine<Object, Object> caffeine) {
        throw new IllegalArgumentException("mica-caffeine not support customization Caffeine bean\uff0cyou can customize CaffeineSpec bean.");
    }

    public void setCaffeineSpec(CaffeineSpec caffeineSpec) {
        super.setCaffeineSpec(caffeineSpec);
        this.caffeineSpec = caffeineSpec;
    }

    public void setCacheSpecification(String cacheSpecification) {
        super.setCacheSpecification(cacheSpecification);
        this.caffeineSpec = CaffeineSpec.parse((String)cacheSpecification);
    }

    protected Cache<Object, Object> createNativeCaffeineCache(String name) {
        String[] cacheArray = name.split("#");
        if (cacheArray.length < 2) {
            return super.createNativeCaffeineCache(name);
        }
        Duration duration = DurationStyle.detectAndParse((String)cacheArray[1], (ChronoUnit)ChronoUnit.SECONDS);
        Caffeine cacheBuilder = this.caffeineSpec != null ? Caffeine.from((CaffeineSpec)this.caffeineSpec) : Caffeine.newBuilder();
        CacheLoader<Object, Object> cacheLoader = this.getCacheLoader();
        if (cacheLoader == null) {
            return cacheBuilder.expireAfterAccess(duration).build();
        }
        return cacheBuilder.expireAfterAccess(duration).build(cacheLoader);
    }

    static {
        CACHE_LOADER_FIELD.setAccessible(true);
    }
}

