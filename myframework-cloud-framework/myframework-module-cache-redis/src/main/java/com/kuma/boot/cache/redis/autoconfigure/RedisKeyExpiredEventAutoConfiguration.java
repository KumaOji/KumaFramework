/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.redisson.api.RedissonClient
 *  org.redisson.spring.data.connection.RedissonConnectionFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.event.EventListener
 *  org.springframework.data.redis.connection.RedisConnectionFactory
 *  org.springframework.data.redis.core.RedisKeyExpiredEvent
 *  org.springframework.data.redis.listener.KeyExpirationEventMessageListener
 *  org.springframework.data.redis.listener.RedisMessageListenerContainer
 *  org.springframework.scheduling.annotation.Async
 */
package com.kuma.boot.cache.redis.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import org.redisson.api.RedissonClient;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.scheduling.annotation.Async;

@AutoConfiguration
@ConditionalOnBean(value={RedissonClient.class})
@ConditionalOnProperty(prefix="kuma.boot.cache.redis.key-expired-event.enable", value={"true"}, matchIfMissing=true)
public class RedisKeyExpiredEventAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(RedisKeyExpiredEventAutoConfiguration.class, (String)"kuma-boot-starter-cache-redis", (String[])new String[0]);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedissonConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory((RedisConnectionFactory)connectionFactory);
        return container;
    }

    @Bean
    @ConditionalOnMissingBean
    public KeyExpirationEventMessageListener keyExpirationEventMessageListener(RedisMessageListenerContainer listenerContainer) {
        return new KeyExpirationEventMessageListener(listenerContainer);
    }

    @Async
    @EventListener
    public void onRedisKeyExpiredEvent(RedisKeyExpiredEvent<Object> event) {
        LogUtils.info((String)event.toString(), (Object[])new Object[0]);
    }
}

