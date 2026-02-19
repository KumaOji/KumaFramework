/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.redisson.api.RedissonClient
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication$Type
 *  org.springframework.context.annotation.Bean
 */
package com.kuma.boot.cache.redis.autoconfigure;

import com.kuma.boot.cache.redis.redisson.RedisDelayQueue;
import com.kuma.boot.cache.redis.redisson.RedisDelayQueueRunner;
import com.kuma.boot.common.utils.log.LogUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnWebApplication(type=ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnBean(value={RedissonClient.class})
public class RedisDelayQueueAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(RedisDelayQueueAutoConfiguration.class, (String)"kuma-boot-starter-cache-redis", (String[])new String[0]);
    }

    @Bean
    public RedisDelayQueue redisDelayQueue(RedissonClient redissonClient) {
        return new RedisDelayQueue(redissonClient);
    }

    @Bean
    public RedisDelayQueueRunner redisDelayQueueRunner(RedisDelayQueue redisDelayQueue) {
        return new RedisDelayQueueRunner(redisDelayQueue);
    }
}

