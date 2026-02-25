/*
 *  com.kuma.boot.common.constant.CommonConstants
 *  org.springframework.core.env.Environment
 *  org.springframework.data.redis.core.StringRedisTemplate
 *  org.springframework.data.redis.core.script.RedisScript
 */
package com.kuma.boot.ratelimit.ratelimitredis;

import com.kuma.boot.common.constant.CommonConstants;

import java.lang.invoke.CallSite;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

public class RedisRateLimiterClient
implements RateLimiterClient {
    private static final String REDIS_KEY_PREFIX = "limiter:";
    private static final long FAIL_CODE = 0L;
    private final StringRedisTemplate redisTemplate;
    private final RedisScript<List<Long>> script;
    private final Environment environment;

    public RedisRateLimiterClient(StringRedisTemplate redisTemplate, RedisScript<List<Long>> script, Environment environment) {
        this.redisTemplate = redisTemplate;
        this.script = script;
        this.environment = environment;
    }

    @Override
    public boolean isAllowed(String key, long max, long ttl, TimeUnit timeUnit) {
        String redisKeyBuilder = REDIS_KEY_PREFIX + RedisRateLimiterClient.getApplicationName(this.environment) + ":" + key;
        List<CallSite> keys = Collections.singletonList(redisKeyBuilder);
        long now = System.currentTimeMillis();
        long ttlMillis = timeUnit.toMillis(ttl);
        List results = (List)this.redisTemplate.execute(this.script, keys, new Object[]{"" + max, "" + ttlMillis, "" + now});
        if (results == null || results.isEmpty()) {
            return false;
        }
        Long result = (Long)results.get(0);
        return result != 0L;
    }

    private static String getApplicationName(Environment environment) {
        return environment.getProperty(CommonConstants.SPRING_APP_NAME_KEY, "");
    }
}

