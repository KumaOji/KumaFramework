/*
 *  com.kuma.boot.cache.redis.autoconfigure.RedisAutoConfiguration
 *  org.redisson.api.RedissonClient
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.kuma.boot.ratelimit.ratelimitprovider;

import com.kuma.boot.cache.redis.autoconfigure.RedisAutoConfiguration;
import com.kuma.boot.ratelimit.ratelimitaspect.LimitProperties;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(value={LimitProperties.class})
@ConditionalOnProperty(prefix="kuma.boot.ratelimit", name={"enabled"}, havingValue="true", matchIfMissing=true)
@ConditionalOnClass(value={RedisAutoConfiguration.class})
@AutoConfiguration(after={RedisAutoConfiguration.class})
public class RateLimiterAutoConfiguration {
    @Bean
    public BizKeyProvider bizKeyProvider() {
        return new BizKeyProvider();
    }

    @Bean
    public RateLimiterService rateLimiterService(BizKeyProvider bizKeyProvider) {
        return new RateLimiterService(bizKeyProvider);
    }

    @Configuration
    @ConditionalOnClass(value={RedissonClient.class})
    public static class RateLimitAspectHandlerConfiguration {
        @Bean
        public RateLimitAspectHandler rateLimitAspectHandler(RedissonClient client, RateLimiterService rateLimiterService) {
            return new RateLimitAspectHandler(client, rateLimiterService);
        }
    }
}

