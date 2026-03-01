//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.cache.jetcache.stamp;

import com.alicp.jetcache.AutoReleaseLock;
import com.kuma.boot.cache.jetcache.exception.StampDeleteFailedException;
import com.kuma.boot.cache.jetcache.exception.StampHasExpiredException;
import com.kuma.boot.cache.jetcache.exception.StampMismatchException;
import com.kuma.boot.cache.jetcache.exception.StampParameterIllegalException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.InitializingBean;

public interface StampManager<K, V> extends InitializingBean {
    Duration getExpire();

    void put(K key, V value, long expireAfterWrite, TimeUnit timeUnit);

    default void put(K key, V value, Duration expire) {
        this.put(key, value, expire.toMillis(), TimeUnit.MILLISECONDS);
    }

    default void put(K key, V value) {
        this.put(key, value, this.getExpire());
    }

    V nextStamp(K key);

    default V create(K key, long expireAfterWrite, TimeUnit timeUnit) {
        V value = (V)this.nextStamp(key);
        this.put(key, value, expireAfterWrite, timeUnit);
        return value;
    }

    default V create(K key, Duration expire) {
        return (V)this.create(key, expire.toMillis(), TimeUnit.MILLISECONDS);
    }

    default V create(K key) {
        return (V)this.create(key, this.getExpire());
    }

    boolean check(K key, V value) throws StampParameterIllegalException, StampHasExpiredException, StampMismatchException;

    V get(K key);

    void delete(K key) throws StampDeleteFailedException;

    default boolean containKey(K key) {
        V value = (V)this.get(key);
        return ObjectUtils.isNotEmpty(value);
    }

    AutoReleaseLock lock(K key, long expire, TimeUnit timeUnit);

    default AutoReleaseLock lock(K key, Duration expire) {
        return this.lock(key, expire.toMillis(), TimeUnit.MILLISECONDS);
    }

    default AutoReleaseLock lock(K key) {
        return this.lock(key, this.getExpire());
    }

    boolean lockAndRun(K key, long expire, TimeUnit timeUnit, Runnable action);

    default boolean lockAndRun(K key, Duration expire, Runnable action) {
        return this.lockAndRun(key, expire.toMillis(), TimeUnit.MILLISECONDS, action);
    }

    default boolean lockAndRun(K key, Runnable action) {
        return this.lockAndRun(key, this.getExpire(), action);
    }
}
