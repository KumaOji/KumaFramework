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
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * {@link StampManager} 抽象基类，基于 JetCache 实现存储和分布式锁。
 *
 * @param <K> key 类型
 * @param <V> stamp 值类型
 * @author kuma
 * @since 2022-07-03
 */
public abstract class AbstractStampManager<K, V> implements StampManager<K, V> {

    private static final Duration DEFAULT_EXPIRE = Duration.ofMinutes(30);

    private final String cacheName;
    private final CacheType cacheType;
    private Duration expire;
    private final Cache<K, V> cache;

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
        this.cache = JetCacheUtils.create(cacheName, cacheType, expire);
    }

    protected Cache<K, V> getCache() {
        return cache;
    }

    @Override
    public Duration getExpire() {
        return expire;
    }

    public void setExpire(Duration expire) {
        this.expire = expire;
    }

    @Override
    public boolean check(K key, V value) {
        if (value == null) {
            throw new StampParameterIllegalException("Parameter Stamp value is null");
        }
        V storedStamp = get(key);
        if (storedStamp == null) {
            throw new StampHasExpiredException("Stamp is invalid!");
        }
        if (!Objects.equals(storedStamp, value)) {
            throw new StampMismatchException("Stamp is mismatch!");
        }
        return true;
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public void delete(K key) throws StampDeleteFailedException {
        if (!cache.remove(key)) {
            throw new StampDeleteFailedException("Delete Stamp From Storage Failed");
        }
    }

    @Override
    public void put(K key, V value, long expireAfterWrite, TimeUnit timeUnit) {
        cache.put(key, value, expireAfterWrite, timeUnit);
    }

    @Override
    public AutoReleaseLock lock(K key, long expire, TimeUnit timeUnit) {
        return cache.tryLock(key, expire, timeUnit);
    }

    @Override
    public boolean lockAndRun(K key, long expire, TimeUnit timeUnit, Runnable action) {
        return cache.tryLockAndRun(key, expire, timeUnit, action);
    }
}
