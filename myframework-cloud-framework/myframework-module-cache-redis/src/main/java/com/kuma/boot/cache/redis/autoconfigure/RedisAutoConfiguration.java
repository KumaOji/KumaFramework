/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.json.JacksonUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.redisson.spring.data.connection.RedissonConnectionFactory
 *  org.redisson.spring.starter.RedissonAutoConfigurationV4
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.ObjectProvider
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration
 *  org.springframework.boot.data.redis.autoconfigure.DataRedisProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Role
 *  org.springframework.data.redis.connection.RedisConnectionFactory
 *  org.springframework.data.redis.core.RedisTemplate
 *  org.springframework.data.redis.core.ValueOperations
 *  org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
 *  org.springframework.data.redis.serializer.JdkSerializationRedisSerializer
 *  org.springframework.data.redis.serializer.RedisSerializer
 *  org.springframework.data.redis.serializer.StringRedisSerializer
 *  tools.jackson.databind.ObjectMapper
 *  tools.jackson.databind.json.JsonMapper
 */
package com.kuma.boot.cache.redis.autoconfigure;

import com.kuma.boot.cache.redis.autoconfigure.properties.CacheManagerProperties;
import com.kuma.boot.cache.redis.autoconfigure.properties.RedisProperties;
import com.kuma.boot.cache.redis.enums.SerializerType;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.redisson.spring.starter.RedissonAutoConfigurationV4;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.boot.data.redis.autoconfigure.DataRedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@AutoConfiguration(after={DataRedisAutoConfiguration.class, RedissonAutoConfigurationV4.class})
@ConditionalOnProperty(prefix="kuma.boot.cache.redis", name={"enabled"}, havingValue="true", matchIfMissing=true)
@EnableConfigurationProperties(value={DataRedisProperties.class, CacheManagerProperties.class, RedisProperties.class})
@Role(value=2)
public class RedisAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(RedisAutoConfiguration.class, (String)"kuma-boot-starter-cache-redis", (String[])new String[0]);
    }

    @Bean
    public RedisSerializer<Object> redisSerializer(CacheManagerProperties properties, ObjectProvider<JsonMapper> objectProvider) {
        SerializerType serializerType = properties.getSerializerType();
        if (SerializerType.JDK == serializerType) {
            ClassLoader classLoader = this.getClass().getClassLoader();
            return new JdkSerializationRedisSerializer(classLoader);
        }
        return new GenericJacksonJsonRedisSerializer((ObjectMapper)JacksonUtils.MAPPER);
    }

    @Bean
    public RedisTemplate<String, Object> stringObjectRedisTemplate(RedissonConnectionFactory factory, RedisSerializer<Object> redisSerializer) {
        RedisTemplate template = new RedisTemplate();
        template.setConnectionFactory((RedisConnectionFactory)factory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer((RedisSerializer)stringRedisSerializer);
        template.setHashKeySerializer((RedisSerializer)stringRedisSerializer);
        template.setValueSerializer(redisSerializer);
        template.setHashValueSerializer(redisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisRepository redisRepository(RedisTemplate<String, Object> stringObjectRedisTemplate) {
        return new RedisRepository(stringObjectRedisTemplate, false);
    }

    @Bean
    @ConditionalOnMissingBean(value={ValueOperations.class})
    public ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> stringObjectRedisTemplate) {
        return stringObjectRedisTemplate.opsForValue();
    }
}

