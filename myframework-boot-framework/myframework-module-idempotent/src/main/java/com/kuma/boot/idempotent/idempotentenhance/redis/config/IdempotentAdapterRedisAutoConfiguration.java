/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.NonNull
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.data.redis.connection.RedisConnectionFactory
 *  org.springframework.data.redis.core.StringRedisTemplate
 */
package com.kuma.boot.idempotent.idempotentenhance.redis.config;

import com.kuma.boot.idempotent.idempotentenhance.core.config.properties.IdempotentCoreProperties;
import com.kuma.boot.idempotent.idempotentenhance.core.handler.IdempotentExceptionEventHandler;
import com.kuma.boot.idempotent.idempotentenhance.core.helper.IdempotentHelper;
import com.kuma.boot.idempotent.idempotentenhance.core.repository.IdempotentRepository;
import com.kuma.boot.idempotent.idempotentenhance.redis.RedisIdempotentRepositoryImpl;
import com.kuma.boot.idempotent.idempotentenhance.redis.config.properties.IdempotentRedisAdapterProperties;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@EnableConfigurationProperties(value={IdempotentRedisAdapterProperties.class})
public class IdempotentAdapterRedisAutoConfiguration {
    @Bean(name={"idempotentStringRedisTemplate"})
    @ConditionalOnMissingBean(name={"idempotentStringRedisTemplate"})
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean(name={"redisIdempotentRepository"})
    @ConditionalOnMissingBean(name={"redisIdempotentRepository"})
    public IdempotentRepository redisIdempotentRepository(@Qualifier(value="idempotentStringRedisTemplate") @NonNull StringRedisTemplate redisTemplate, @NonNull IdempotentRedisAdapterProperties properties) {
        return new RedisIdempotentRepositoryImpl(redisTemplate, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public IdempotentHelper idempotentHelper(@NonNull IdempotentRepository idempotentRepository, @NonNull IdempotentExceptionEventHandler exceptionEventHandler, @NonNull IdempotentCoreProperties idempotentCoreProperties) {
        return new IdempotentHelper(exceptionEventHandler, idempotentRepository, idempotentCoreProperties);
    }
}

