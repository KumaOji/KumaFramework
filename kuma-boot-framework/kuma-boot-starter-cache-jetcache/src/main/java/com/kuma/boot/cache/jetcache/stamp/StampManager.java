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
import com.kuma.boot.cache.jetcache.exception.StampDeleteFailedException;
import com.kuma.boot.cache.jetcache.exception.StampHasExpiredException;
import com.kuma.boot.cache.jetcache.exception.StampMismatchException;
import com.kuma.boot.cache.jetcache.exception.StampParameterIllegalException;
import org.springframework.beans.factory.InitializingBean;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Stamp（令牌/验证码）管理接口，提供存储、验证、锁定等操作。
 *
 * @param <K> key 类型
 * @param <V> stamp 值类型
 * @author kuma
 * @since 2022-07-03
 */
public interface StampManager<K, V> extends InitializingBean {

    Duration getExpire();

    void put(K key, V value, long expireAfterWrite, TimeUnit timeUnit);

    default void put(K key, V value, Duration expire) {
        put(key, value, expire.toMillis(), TimeUnit.MILLISECONDS);
    }

    default void put(K key, V value) {
        put(key, value, getExpire());
    }

    V nextStamp(K key);

    default V create(K key, long expireAfterWrite, TimeUnit timeUnit) {
        V value = nextStamp(key);
        put(key, value, expireAfterWrite, timeUnit);
        return value;
    }

    default V create(K key, Duration expire) {
        return create(key, expire.toMillis(), TimeUnit.MILLISECONDS);
    }

    default V create(K key) {
        return create(key, getExpire());
    }

    boolean check(K key, V value) throws StampParameterIllegalException, StampHasExpiredException, StampMismatchException;

    V get(K key);

    void delete(K key) throws StampDeleteFailedException;

    default boolean containKey(K key) {
        return get(key) != null;
    }

    AutoReleaseLock lock(K key, long expire, TimeUnit timeUnit);

    default AutoReleaseLock lock(K key, Duration expire) {
        return lock(key, expire.toMillis(), TimeUnit.MILLISECONDS);
    }

    default AutoReleaseLock lock(K key) {
        return lock(key, getExpire());
    }

    boolean lockAndRun(K key, long expire, TimeUnit timeUnit, Runnable action);

    default boolean lockAndRun(K key, Duration expire, Runnable action) {
        return lockAndRun(key, expire.toMillis(), TimeUnit.MILLISECONDS, action);
    }

    default boolean lockAndRun(K key, Runnable action) {
        return lockAndRun(key, getExpire(), action);
    }
}
