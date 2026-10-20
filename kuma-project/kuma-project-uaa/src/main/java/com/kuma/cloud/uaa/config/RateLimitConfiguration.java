package com.kuma.cloud.uaa.config;

import com.kuma.boot.ratelimit.ratelimitredis.RedisRateLimiterAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({
        RedisRateLimiterAutoConfiguration.class
})
public class RateLimitConfiguration {
}
