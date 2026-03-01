/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.cache.redis.autoconfigure.RedisAutoConfiguration
 *  com.kuma.boot.common.utils.json.JacksonUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  me.zhyd.oauth.cache.AuthDefaultStateCache
 *  me.zhyd.oauth.cache.AuthStateCache
 *  org.springframework.boot.autoconfigure.AutoConfigureBefore
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.context.annotation.Bean
 *  org.springframework.data.redis.connection.RedisConnectionFactory
 *  org.springframework.data.redis.core.RedisTemplate
 *  org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
 *  org.springframework.data.redis.serializer.RedisSerializer
 *  org.springframework.data.redis.serializer.StringRedisSerializer
 *  tools.jackson.databind.ObjectMapper
 */
package com.kuma.boot.security.justauth.autoconfigure;

import com.kuma.boot.cache.redis.autoconfigure.RedisAutoConfiguration;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.justauth.autoconfigure.properties.JustAuthProperties;
import com.kuma.boot.security.justauth.cache.RedisStateCache;
import me.zhyd.oauth.cache.AuthDefaultStateCache;
import me.zhyd.oauth.cache.AuthStateCache;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper;

public class JustAuthStateCacheConfiguration {

    @ConditionalOnProperty(name={"justauth.cache.type"}, havingValue="custom")
    static class Custom {
        Custom() {
        }

        @Bean
        @ConditionalOnMissingBean(value={AuthStateCache.class})
        public AuthStateCache authStateCache() {
            LogUtils.error((String)"\u8bf7\u81ea\u884c\u5b9e\u73b0 me.zhyd.oauth.cache.AuthStateCache", (Object[])new Object[0]);
            throw new RuntimeException();
        }

        static {
            LogUtils.debug((String)"JustAuth \u4f7f\u7528 \u81ea\u5b9a\u4e49\u7f13\u5b58\u5b58\u50a8 state \u6570\u636e", (Object[])new Object[0]);
        }
    }

    @ConditionalOnMissingBean(value={AuthStateCache.class})
    @ConditionalOnProperty(name={"justauth.cache.type"}, havingValue="default", matchIfMissing=true)
    static class Default {
        Default() {
        }

        @Bean
        public AuthStateCache authStateCache() {
            return AuthDefaultStateCache.INSTANCE;
        }

        static {
            LogUtils.debug((String)"JustAuth \u4f7f\u7528 \u9ed8\u8ba4\u7f13\u5b58\u5b58\u50a8 state \u6570\u636e", (Object[])new Object[0]);
        }
    }

    @ConditionalOnClass(value={RedisTemplate.class})
    @ConditionalOnMissingBean(value={AuthStateCache.class})
    @AutoConfigureBefore(value={RedisAutoConfiguration.class})
    @ConditionalOnProperty(name={"justauth.cache.type"}, havingValue="redis", matchIfMissing=true)
    static class Redis {
        Redis() {
        }

        @Bean(name={"justAuthRedisCacheTemplate"})
        public RedisTemplate<String, String> justAuthRedisCacheTemplate(RedisConnectionFactory redisConnectionFactory) {
            RedisTemplate template = new RedisTemplate();
            template.setKeySerializer((RedisSerializer)new StringRedisSerializer());
            template.setValueSerializer((RedisSerializer)new GenericJacksonJsonRedisSerializer((ObjectMapper)JacksonUtils.MAPPER));
            template.setConnectionFactory(redisConnectionFactory);
            return template;
        }

        @Bean
        public AuthStateCache authStateCache(RedisTemplate<String, String> justAuthRedisCacheTemplate, JustAuthProperties justAuthProperties) {
            return new RedisStateCache(justAuthRedisCacheTemplate, justAuthProperties.getCache());
        }

        static {
            LogUtils.debug((String)"JustAuth \u4f7f\u7528 Redis \u7f13\u5b58\u5b58\u50a8 state \u6570\u636e", (Object[])new Object[0]);
        }
    }
}

