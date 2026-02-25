/*
 *  org.redisson.api.RedissonClient
 *  org.redisson.spring.starter.RedissonAutoConfigurationV2
 *  org.springframework.beans.factory.ObjectProvider
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.context.annotation.Bean
 */
package com.kuma.boot.ratelimit.ratelimitredisson;

import com.kuma.boot.ratelimit.ratelimitredisson.executor.RedissonLimitExecutor;
import com.kuma.boot.ratelimit.ratelimitredisson.interceptor.LimitInterceptor;
import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class LimitAutoConfiguration {
    @Bean
    public LimitInterceptor limitInterceptor(ObjectProvider<LimitExecutor> provider) {
        return new LimitInterceptor(provider);
    }

    @ConditionalOnClass(value={RedissonClient.class})
    @AutoConfiguration(after={RedissonAutoConfigurationV2.class}, afterName={"org.redisson.spring.starter.RedissonAutoConfigurationV2"})
    public static class RedissonLimitConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public LimitExecutor redissonLimitExecutor(RedissonClient redissonClient) {
            return new RedissonLimitExecutor(redissonClient);
        }
    }
}

