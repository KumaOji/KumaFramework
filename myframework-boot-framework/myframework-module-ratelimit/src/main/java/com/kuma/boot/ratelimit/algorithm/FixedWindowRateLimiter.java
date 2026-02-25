/*
 *  org.redisson.api.RAtomicLong
 *  org.redisson.api.RLock
 *  org.redisson.api.RedissonClient
 */
package com.kuma.boot.ratelimit.algorithm;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

public class FixedWindowRateLimiter {
    public static final String KEY = "fixedWindowRateLimiter:";
    private RedissonClient redissonClient;
    private Long limit;
    private Long windowSize;

    public FixedWindowRateLimiter(Long limit, Long windowSize) {
        this.limit = limit;
        this.windowSize = windowSize;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean triggerLimit(String path) {
        RLock rLock = this.redissonClient.getLock("fixedWindowRateLimiter:LOCK:" + path);
        try {
            rLock.lock(100L, TimeUnit.MILLISECONDS);
            String redisKey = KEY + path;
            RAtomicLong counter = this.redissonClient.getAtomicLong(redisKey);
            long count = counter.incrementAndGet();
            if (count == 1L) {
                counter.expire(Duration.ofSeconds(this.windowSize));
            }
            if (count > this.limit) {
                counter.decrementAndGet();
                boolean bl = true;
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            rLock.unlock();
        }
    }
}

