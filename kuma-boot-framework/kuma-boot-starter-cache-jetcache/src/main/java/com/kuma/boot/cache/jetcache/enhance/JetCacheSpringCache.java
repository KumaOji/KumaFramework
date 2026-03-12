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

package com.kuma.boot.cache.jetcache.enhance;

import com.alicp.jetcache.Cache;
import com.kuma.boot.common.utils.json.JacksonUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.support.AbstractValueAdaptingCache;

import java.util.concurrent.Callable;

/**
 * 将 JetCache {@link Cache} 适配为 Spring {@link org.springframework.cache.Cache}。
 *
 * @author kuma
 * @since 2022-07-03
 */
public class JetCacheSpringCache extends AbstractValueAdaptingCache {

    private static final Logger log = LoggerFactory.getLogger(JetCacheSpringCache.class);

    private final String cacheName;
    private final Cache<Object, Object> cache;

    public JetCacheSpringCache(String cacheName, Cache<Object, Object> cache, boolean allowNullValues) {
        super(allowNullValues);
        this.cacheName = cacheName;
        this.cache = cache;
    }

    @Override
    public String getName() {
        return cacheName;
    }

    @Override
    public Cache<Object, Object> getNativeCache() {
        return cache;
    }

    @Override
    protected @Nullable Object lookup(Object key) {
        Object value = cache.get(key);
        if (value != null) {
            log.trace("[kmc] |- CACHE - Lookup in herodotus cache, value: [{}]", JacksonUtils.toJson(value));
            return value;
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> @Nullable T get(Object key, Callable<T> valueLoader) {
        log.trace("[kmc] |- CACHE - Get from herodotus cache, key: {}", key);
        return (T) fromStoreValue(cache.computeIfAbsent(key, k -> {
            try {
                return toStoreValue(valueLoader.call());
            } catch (Throwable ex) {
                throw new ValueRetrievalException(key, valueLoader, ex);
            }
        }));
    }

    @Override
    public void put(Object key, @Nullable Object value) {
        log.trace("[kmc] |- CACHE - Put into herodotus cache, key: {}", key);
        cache.put(key, toStoreValue(value));
    }

    @Override
    public @Nullable ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        log.trace("[kmc] |- CACHE - PutIfAbsent into herodotus cache, key: {}", key);
        Object existing = cache.putIfAbsent(key, toStoreValue(value));
        return toValueWrapper(existing);
    }

    @Override
    public void evict(Object key) {
        log.trace("[kmc] |- CACHE - Evict from herodotus cache, key: {}", key);
        cache.remove(key);
    }

    @Override
    public boolean evictIfPresent(Object key) {
        log.trace("[kmc] |- CACHE - EvictIfPresent from herodotus cache, key: {}", key);
        return cache.remove(key);
    }

    @Override
    public void clear() {
        log.trace("[kmc] |- CACHE - Clear herodotus cache.");
        cache.close();
    }
}
