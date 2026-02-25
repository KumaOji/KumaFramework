/*
 *  org.redisson.api.RRateLimiter
 *  org.redisson.api.RateType
 *  org.redisson.api.RedissonClient
 */
package com.kuma.boot.ratelimit.algorithm;

import java.time.Duration;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

public class TokenBucketRateLimiter {
    public static final String KEY = "TokenBucketRateLimiter:";
    private RedissonClient redissonClient;
    private Long limit;
    private Long tokenRate;

    public TokenBucketRateLimiter(Long limit, Long tokenRate) {
        this.limit = limit;
        this.tokenRate = tokenRate;
    }

    public boolean triggerLimit(String path) {
        RRateLimiter rateLimiter = this.redissonClient.getRateLimiter(KEY + path);
        rateLimiter.trySetRate(RateType.OVERALL, this.limit.longValue(), Duration.ofSeconds(this.tokenRate));
        return rateLimiter.tryAcquire();
    }
}

