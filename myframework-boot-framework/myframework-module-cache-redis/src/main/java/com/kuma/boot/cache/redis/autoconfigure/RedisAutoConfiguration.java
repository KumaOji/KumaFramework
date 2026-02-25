/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.cache.redis.autoconfigure;

import com.kuma.boot.cache.redis.autoconfigure.properties.RedisProperties;
import com.kuma.boot.cache.redis.lock.RedissonDistributedLock;
import com.kuma.boot.lock.support.DistributedLock;
import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfigurationV4;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.boot.data.redis.autoconfigure.DataRedisProperties;
import org.springframework.context.annotation.Role;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import com.kuma.boot.cache.redis.enums.SerializerType;
import com.kuma.boot.cache.redis.autoconfigure.properties.CacheManagerProperties;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.json.JsonMapper;

/**
 * redis 自动配置类
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-07 21:17:02
 */
@AutoConfiguration(after = {DataRedisAutoConfiguration.class, RedissonAutoConfigurationV4.class})
@ConditionalOnProperty(
        prefix = RedisProperties.PREFIX,
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
@EnableConfigurationProperties({
        DataRedisProperties.class,
        CacheManagerProperties.class,
        RedisProperties.class
})
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class RedisAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(RedisAutoConfiguration.class, StarterNameConstants.CACHE_REDIS_STARTER);
    }

    // @Bean
    // public RedisSerializer<String> redisKeySerializer() {
    //	return RedisSerializer.string();
    // }

    @Bean
    public RedisSerializer<Object> redisSerializer(
            CacheManagerProperties properties, ObjectProvider<JsonMapper> objectProvider) {
        SerializerType serializerType = properties.getSerializerType();

        if (SerializerType.JDK == serializerType) {
            ClassLoader classLoader = this.getClass().getClassLoader();
            return new JdkSerializationRedisSerializer(classLoader);
        }

        //// jackson findAndRegisterModules，use copy
        // JsonMapper jsonMapper = objectProvider.getIfAvailable(JsonMapper::new).copy();
        // jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //// findAndRegisterModules
        // jsonMapper.findAndRegisterModules();
        //// class type info to json
        // GenericJackson2JsonRedisSerializer.registerNullValueSerializer(jsonMapper, null);
        // jsonMapper.activateDefaultTyping(jsonMapper.getPolymorphicTypeValidator(),
        //	DefaultTyping.NON_FINAL, As.PROPERTY);

        return new GenericJacksonJsonRedisSerializer(JacksonUtils.MAPPER);
    }

//    @Bean
//	@ConditionalOnSingleCandidate(RedissonConnectionFactory.class)
//    public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redissonClient) {
//        return new RedissonConnectionFactory(redissonClient);
//    }

    /**
     * 创建 RedisTemplate，bean 名称为 redisTemplate。
     * 使用 redisTemplate 名称以满足 redisKeyValueAdapter 等 Spring Data Redis 组件的依赖要求。
     */
    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(
            RedissonConnectionFactory factory, RedisSerializer<Object> redisSerializer) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);

        // value序列化方式采用jackson
        template.setValueSerializer(redisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(redisSerializer);
        template.afterPropertiesSet();

        return template;
    }

    @Bean
    public RedisRepository redisRepository(RedisTemplate<String, Object> redisTemplate) {
        return new RedisRepository(redisTemplate, false);
    }

    @Bean
    @ConditionalOnMissingBean(ValueOperations.class)
    public ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    @Bean
    @ConditionalOnBean(RedissonClient.class)
    @ConditionalOnMissingBean(DistributedLock.class)
    public DistributedLock distributedLock(RedissonClient redissonClient) {
        return new RedissonDistributedLock(redissonClient);
    }

//    @Bean("stringRedisTemplate")
//	@ConditionalOnMissingBean(StringRedisTemplate.class)
//    public StringRedisTemplate stringRedisTemplate(RedissonConnectionFactory factory) {
//        StringRedisTemplate template = new StringRedisTemplate();
//        template.setConnectionFactory(factory);
//        return template;
//    }
}
