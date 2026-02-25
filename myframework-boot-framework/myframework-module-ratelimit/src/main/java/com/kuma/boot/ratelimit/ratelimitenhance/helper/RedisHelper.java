/*
 *  jakarta.annotation.Resource
 *  org.springframework.data.redis.core.RedisTemplate
 *  org.springframework.data.redis.core.script.RedisScript
 */
package com.kuma.boot.ratelimit.ratelimitenhance.helper;

import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

public class RedisHelper {
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public <T> T executeScript(RedisScript<T> redisScript, List<String> keys, List<Object> args) {
        return (T)this.redisTemplate.execute(redisScript, keys, args.toArray());
    }
}

