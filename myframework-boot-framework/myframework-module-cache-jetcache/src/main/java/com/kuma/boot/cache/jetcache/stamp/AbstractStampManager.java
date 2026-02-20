//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.cache.jetcache.stamp;

import com.alicp.jetcache.AutoReleaseLock;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.kuma.boot.cache.jetcache.exception.StampDeleteFailedException;
import com.kuma.boot.cache.jetcache.exception.StampHasExpiredException;
import com.kuma.boot.cache.jetcache.exception.StampMismatchException;
import com.kuma.boot.cache.jetcache.exception.StampParameterIllegalException;
import com.kuma.boot.cache.jetcache.utils.JetCacheUtils;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.ObjectUtils;

public abstract class AbstractStampManager<K, V> implements StampManager<K, V> {
    private static final Duration DEFAULT_EXPIRE = Duration.ofMinutes(30L);
    private String cacheName;
    private CacheType cacheType;
    private Duration expire;
    private Cache<K, V> cache;

    public AbstractStampManager(String cacheName) {
        this(cacheName, CacheType.BOTH);
    }

    public AbstractStampManager(String cacheName, CacheType cacheType) {
        this(cacheName, cacheType, DEFAULT_EXPIRE);
    }

    public AbstractStampManager(String cacheName, CacheType cacheType, Duration expire) {
        this.cacheName = cacheName;
        this.cacheType = cacheType;
        this.expire = expire;
        this.cache = JetCacheUtils.create(this.cacheName, this.cacheType, this.expire);
    }

    protected Cache<K, V> getCache() {
        return this.cache;
    }

    public Duration getExpire() {
        return this.expire;
    }

    public void setExpire(Duration expire) {
        this.expire = expire;
    }

    public boolean check(K key, V value) {
        if (ObjectUtils.isEmpty(value)) {
            throw new StampParameterIllegalException("Parameter Stamp value is null");
        } else {
            V storedStamp = (V)this.get(key);
            if (ObjectUtils.isEmpty(storedStamp)) {
                throw new StampHasExpiredException("Stamp is invalid!");
            } else if (ObjectUtils.notEqual(storedStamp, value)) {
                throw new StampMismatchException("Stamp is mismathch!");
            } else {
                return true;
            }
        }
    }

    public V get(K key) {
        return (V)this.getCache().get(key);
    }

    public void delete(K key) throws StampDeleteFailedException {
        boolean result = this.getCache().remove(key);
        if (!result) {
            throw new StampDeleteFailedException("Delete Stamp From Storage Failed");
        }
    }

    public void put(K key, V value, long expireAfterWrite, TimeUnit timeUnit) {
        this.getCache().put(key, value, expireAfterWrite, timeUnit);
    }

    public AutoReleaseLock lock(K key, long expire, TimeUnit timeUnit) {
        return this.getCache().tryLock(key, expire, timeUnit);
    }

    public boolean lockAndRun(K key, long expire, TimeUnit timeUnit, Runnable action) {
        return this.getCache().tryLockAndRun(key, expire, timeUnit, action);
    }
}
