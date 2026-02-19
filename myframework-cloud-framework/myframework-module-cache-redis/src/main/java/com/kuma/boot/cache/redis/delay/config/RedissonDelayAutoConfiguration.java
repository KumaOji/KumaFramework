/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.redisson.api.RedissonClient
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Role
 *  org.springframework.context.annotation.Scope
 */
package com.kuma.boot.cache.redis.delay.config;

import com.kuma.boot.common.utils.log.LogUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.context.annotation.Scope;

@AutoConfiguration
@ConditionalOnBean(value={RedissonClient.class})
public class RedissonDelayAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(RedissonDelayAutoConfiguration.class, (String)"kuma-boot-starter-cache-redis", (String[])new String[0]);
    }

    @Scope(value="singleton")
    @Role(value=2)
    @Bean(name={"com.kuma.cloud.redis.redisson.redisson.internalRedissonListenerAnnotationProcessor"})
    public RedissonAnnotationBeanPostProcessor redissonAnnotationBeanPostProcessor() {
        return new RedissonAnnotationBeanPostProcessor();
    }

    @Scope(value="singleton")
    @Bean(name={"com.kuma.cloud.redis.redisson.redisson.internalRedissonListenerRegistry"})
    public RedissonListenerRegistry redissonListenerRegistry() {
        return new RedissonListenerRegistry();
    }

    @Scope(value="singleton")
    @Bean(name={"com.kuma.cloud.redis.redisson.redisson.internalRedissonQueueBeanProcessor"})
    public RedissonQueueBeanPostProcessor redissonQueueBeanPostProcessor() {
        return new RedissonQueueBeanPostProcessor();
    }

    @Scope(value="singleton")
    @Bean(name={"com.kuma.cloud.redis.redisson.redisson.internalRedissonQueueRegistry"})
    public RedissonQueueRegistry redissonQueueRegistry() {
        return new RedissonQueueRegistry();
    }

    @Scope(value="singleton")
    @Bean
    @ConditionalOnMissingBean
    public RedissonTemplate redissonTemplate() {
        return new RedissonTemplate();
    }
}

