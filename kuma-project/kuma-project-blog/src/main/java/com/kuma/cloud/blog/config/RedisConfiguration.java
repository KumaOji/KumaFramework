package com.kuma.cloud.blog.config;

import com.kuma.boot.cache.redis.autoconfigure.RedisAutoConfiguration;
import com.kuma.boot.cache.redis.autoconfigure.RedisCacheManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/** Explicitly loads the Kuma starter features used by this application. */
@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({
        RedisAutoConfiguration.class,
        RedisCacheManagerAutoConfiguration.class
})
public class RedisConfiguration {
}
