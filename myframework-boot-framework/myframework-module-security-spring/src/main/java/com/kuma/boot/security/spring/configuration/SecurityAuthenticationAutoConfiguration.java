/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  com.kuma.boot.common.utils.log.LogUtils
 *  jakarta.annotation.PostConstruct
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.kuma.boot.security.spring.configuration;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.spring.oauth2.token.OAuth2AccessTokenStore;
import com.kuma.boot.security.spring.properties.OAuth2AuthenticationProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration
@EnableConfigurationProperties(value={OAuth2AuthenticationProperties.class})
public class SecurityAuthenticationAutoConfiguration {
    @PostConstruct
    public void postConstruct() {
        LogUtils.info((String)"SDK [OAuth2 Authentication] Auto Configure.", (Object[])new Object[0]);
    }

    @Configuration
    @ConditionalOnClass(value={RedisRepository.class})
    public static class OAuth2AccessTokenStoreConfiguration {
        @Bean
        @ConditionalOnBean(value={RedisRepository.class})
        public OAuth2AccessTokenStore oAuth2AccessTokenStore(RedisRepository redisRepository) {
            return new OAuth2AccessTokenStore(redisRepository);
        }
    }
}

