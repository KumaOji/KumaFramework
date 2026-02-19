//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.cache.jetcache.enhance;

import com.alicp.jetcache.Cache;
import com.kuma.boot.common.utils.json.JacksonUtils;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.ObjectUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.support.AbstractValueAdaptingCache;

public class JetCacheSpringCache extends AbstractValueAdaptingCache {
    private static final Logger log = LoggerFactory.getLogger(JetCacheSpringCache.class);
    private final String cacheName;
    private final Cache<Object, Object> cache;

    public JetCacheSpringCache(String cacheName, Cache<Object, Object> cache, boolean allowNullValues) {
        super(allowNullValues);
        this.cacheName = cacheName;
        this.cache = cache;
    }

    public String getName() {
        return this.cacheName;
    }

    public final Cache<Object, Object> getNativeCache() {
        return this.cache;
    }

    protected @Nullable Object lookup(Object key) {
        Object value = this.cache.get(key);
        if (ObjectUtils.isNotEmpty(value)) {
            log.trace("[kmc] |- CACHE - Lookup data in herodotus cache, value is : [{}]", JacksonUtils.toJson(value));
            return value;
        } else {
            return null;
        }
    }

    public <T> @Nullable T get(Object key, Callable<T> valueLoader) {
        log.trace("[kmc] |- CACHE - Get data in herodotus cache, key: {}", key);
        return (T)this.fromStoreValue(this.cache.computeIfAbsent(key, (k) -> {
            try {
                return this.toStoreValue(valueLoader.call());
            } catch (Throwable ex) {
                throw new org.springframework.cache.Cache.ValueRetrievalException(key, valueLoader, ex);
            }
        }));
    }

    public void put(Object key, @Nullable Object value) {
        log.trace("[kmc] |- CACHE - Put data in herodotus cache, key: {}", key);
        this.cache.put(key, this.toStoreValue(value));
    }

    public org.springframework.cache.Cache.@Nullable ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        log.trace("[kmc] |- CACHE - PutIfPresent data in herodotus cache, key: {}", key);
        Object existing = this.cache.putIfAbsent(key, this.toStoreValue(value));
        return this.toValueWrapper(existing);
    }

    public void evict(Object key) {
        log.trace("[kmc] |- CACHE - Evict data in herodotus cache, key: {}", key);
        this.cache.remove(key);
    }

    public boolean evictIfPresent(Object key) {
        log.trace("[kmc] |- CACHE - EvictIfPresent data in herodotus cache, key: {}", key);
        return this.cache.remove(key);
    }

    public void clear() {
        log.trace("[kmc] |- CACHE - Clear data in herodotus cache.");
        this.cache.close();
    }
}
