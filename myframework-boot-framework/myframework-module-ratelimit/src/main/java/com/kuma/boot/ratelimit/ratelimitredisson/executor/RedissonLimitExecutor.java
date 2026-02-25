/*
 *  org.redisson.api.RRateLimiter
 *  org.redisson.api.RateType
 *  org.redisson.api.RedissonClient
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.ratelimit.ratelimitredisson.executor;

import com.kuma.boot.ratelimit.ratelimitredisson.LimitExecutor;
import com.kuma.boot.ratelimit.ratelimitredisson.exception.LimitException;

import java.time.Duration;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.util.StringUtils;

public class RedissonLimitExecutor
extends ReentrantLimitExecutor
implements LimitExecutor {
    private final RedissonClient redissonClient;

    public RedissonLimitExecutor(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    protected boolean reentrantTryAccess(String compositeKey, int rate, Duration rateInterval) {
        if (StringUtils.hasText((String)compositeKey)) {
            RRateLimiter limiter = this.redissonClient.getRateLimiter(compositeKey);
            limiter.trySetRate(RateType.OVERALL, (long)rate, rateInterval);
            return limiter.tryAcquire(1L);
        }
        throw new LimitException("Composite key is null or empty");
    }
}

