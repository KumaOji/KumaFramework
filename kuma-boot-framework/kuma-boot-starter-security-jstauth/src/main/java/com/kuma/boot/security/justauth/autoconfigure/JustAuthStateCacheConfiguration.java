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
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class JustAuthStateCacheConfiguration {

    @ConditionalOnProperty(name = {"justauth.cache.type"}, havingValue = "custom")
    static class Custom {

        static {
            LogUtils.debug("JustAuth 使用 自定义缓存存储 state 数据");
        }

        @Bean
        @ConditionalOnMissingBean(AuthStateCache.class)
        public AuthStateCache authStateCache() {
            LogUtils.error("请自行实现 me.zhyd.oauth.cache.AuthStateCache");
            throw new RuntimeException();
        }
    }

    @ConditionalOnMissingBean(AuthStateCache.class)
    @ConditionalOnProperty(name = {"justauth.cache.type"}, havingValue = "default", matchIfMissing = true)
    static class Default {

        static {
            LogUtils.debug("JustAuth 使用 默认缓存存储 state 数据");
        }

        @Bean
        public AuthStateCache authStateCache() {
            return AuthDefaultStateCache.INSTANCE;
        }
    }

    @ConditionalOnClass(RedisTemplate.class)
    @ConditionalOnMissingBean(AuthStateCache.class)
    @AutoConfigureBefore(RedisAutoConfiguration.class)
    @ConditionalOnProperty(name = {"justauth.cache.type"}, havingValue = "redis", matchIfMissing = true)
    static class Redis {

        static {
            LogUtils.debug("JustAuth 使用 Redis 缓存存储 state 数据");
        }

        @Bean(name = {"justAuthRedisCacheTemplate"})
        public RedisTemplate<String, String> justAuthRedisCacheTemplate(RedisConnectionFactory redisConnectionFactory) {
            RedisTemplate<String, String> template = new RedisTemplate<>();
            template.setKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(new GenericJacksonJsonRedisSerializer(JacksonUtils.MAPPER));
            template.setConnectionFactory(redisConnectionFactory);
            return template;
        }

        @Bean
        public AuthStateCache authStateCache(RedisTemplate<String, String> justAuthRedisCacheTemplate,
                                             JustAuthProperties justAuthProperties) {
            return new RedisStateCache(justAuthRedisCacheTemplate, justAuthProperties.getCache());
        }
    }
}
