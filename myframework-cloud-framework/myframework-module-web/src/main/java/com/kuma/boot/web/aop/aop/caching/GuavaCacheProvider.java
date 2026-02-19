/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 */
package com.kuma.boot.web.aop.aop.caching;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class GuavaCacheProvider
implements CacheProvider {
    private static final ConcurrentHashMap<Long, Cache<String, Optional<Object>>> expireMillisToCache = new ConcurrentHashMap();

    @Override
    public <T> void put(String key, T value, long expireMillis) {
        Cache cache = expireMillisToCache.get(expireMillis);
        if (cache == null) {
            cache = expireMillisToCache.computeIfAbsent(expireMillis, k -> CacheBuilder.newBuilder().expireAfterWrite(k.longValue(), TimeUnit.MILLISECONDS).build());
        }
        cache.put((Object)key, Optional.ofNullable(value));
    }

    @Override
    public <T> T get(String key, long expireMillis) {
        Cache<String, Optional<Object>> cache = expireMillisToCache.get(expireMillis);
        if (cache == null) {
            return null;
        }
        Optional optional = (Optional)cache.getIfPresent((Object)key);
        if (optional == null || optional.isEmpty()) {
            return null;
        }
        return optional.get();
    }
}

