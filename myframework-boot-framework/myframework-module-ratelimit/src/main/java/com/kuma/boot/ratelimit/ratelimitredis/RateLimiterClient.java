/*
 *  com.kuma.boot.common.support.function.CheckedSupplier
 *  com.kuma.boot.common.utils.exception.ExceptionUtils
 */
package com.kuma.boot.ratelimit.ratelimitredis;

import com.kuma.boot.common.support.function.CheckedSupplier;
import com.kuma.boot.common.utils.exception.ExceptionUtils;

import java.util.concurrent.TimeUnit;

public interface RateLimiterClient {
    default public boolean isAllowed(String key, long max, long ttl) {
        return this.isAllowed(key, max, ttl, TimeUnit.SECONDS);
    }

    public boolean isAllowed(String var1, long var2, long var4, TimeUnit var6);

    default public <T> T allow(String key, long max, long ttl, CheckedSupplier<T> supplier) {
        return this.allow(key, max, ttl, TimeUnit.SECONDS, supplier);
    }

    default public <T> T allow(String key, long max, long ttl, TimeUnit timeUnit, CheckedSupplier<T> supplier) {
        boolean isAllowed = this.isAllowed(key, max, ttl, timeUnit);
        if (isAllowed) {
            try {
                return (T)supplier.get();
            }
            catch (Throwable e) {
                throw ExceptionUtils.unchecked((Throwable)e);
            }
        }
        throw new RateLimiterException(key, max, ttl, timeUnit);
    }
}

