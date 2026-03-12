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

import com.kuma.boot.cache.redis.autoconfigure.properties.CacheManagerProperties;
import com.kuma.boot.cache.redis.stream.DefaultRStreamTemplate;
import com.kuma.boot.cache.redis.stream.RStreamListenerDetector;
import com.kuma.boot.cache.redis.stream.RStreamTemplate;
import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.model.CharPool;
import com.kuma.boot.common.utils.io.NetUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.time.Duration;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.server.autoconfigure.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;
import org.springframework.util.ErrorHandler;

/**
 * redis Stream 自动配置类
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-03 09:33:26
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = CacheManagerProperties.Stream.PREFIX, name = "enable", havingValue = "true")
public class RedisStreamAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(RedisStreamAutoConfiguration.class, StarterNameConstants.CACHE_REDIS_STARTER);
    }

    @Bean
    @ConditionalOnMissingBean
    public StreamMessageListenerContainerOptions<String, MapRecord<String, String, byte[]>>
    streamMessageListenerContainerOptions(
            CacheManagerProperties properties, ObjectProvider<ErrorHandler> errorHandlerObjectProvider) {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptionsBuilder<
                String, MapRecord<String, String, byte[]>>
                builder = StreamMessageListenerContainerOptions.builder()
                .keySerializer(RedisSerializer.string())
                .hashKeySerializer(RedisSerializer.string())
                .hashValueSerializer(RedisSerializer.byteArray());
        CacheManagerProperties.Stream streamProperties = properties.getStream();
        // 批量大小
        Integer pollBatchSize = streamProperties.getPollBatchSize();
        if (pollBatchSize != null && pollBatchSize > 0) {
            builder.batchSize(pollBatchSize);
        }
        // poll 超时时间
        Duration pollTimeout = streamProperties.getPollTimeout();
        if (pollTimeout != null && !pollTimeout.isNegative()) {
            builder.pollTimeout(pollTimeout);
        }
        // errorHandler
        errorHandlerObjectProvider.ifAvailable(builder::errorHandler);

        // TODO  executor
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public StreamMessageListenerContainer<String, MapRecord<String, String, byte[]>> streamMessageListenerContainer(
            RedissonConnectionFactory redisConnectionFactory,
            StreamMessageListenerContainerOptions<String, MapRecord<String, String, byte[]>>
                    streamMessageListenerContainerOptions) {
        // 根据配置对象创建监听容器
        return StreamMessageListenerContainer.create(redisConnectionFactory, streamMessageListenerContainerOptions);
    }

    @Bean
    @ConditionalOnMissingBean
    public RStreamListenerDetector streamListenerDetector(
            StreamMessageListenerContainer<String, MapRecord<String, String, byte[]>> streamMessageListenerContainer,
            RedisTemplate<String, Object> redisTemplate,
            ObjectProvider<ServerProperties> serverPropertiesObjectProvider,
            CacheManagerProperties properties,
            Environment environment) {
        CacheManagerProperties.Stream streamProperties = properties.getStream();

        // 消费组名称
        String consumerGroup = streamProperties.getConsumerGroup();
        if (StringUtils.isBlank(consumerGroup)) {
            String appName = environment.getRequiredProperty(CommonConstants.SPRING_APP_NAME_KEY);
            String profile = environment.getProperty(CommonConstants.ACTIVE_PROFILES_PROPERTY);
            consumerGroup =
                    StringUtils.isBlank(profile) ? appName + CharPool.COLON : appName + CharPool.COLON + profile;
        }

        // 消费者名称
        String consumerName = streamProperties.getConsumerName();
        if (StringUtils.isBlank(consumerName)) {
            final StringBuilder consumerNameBuilder = new StringBuilder(NetUtils.getHostIp());
            serverPropertiesObjectProvider.ifAvailable(serverProperties -> {
                consumerNameBuilder.append(CharPool.COLON).append(serverProperties.getPort());
            });
            consumerName = consumerNameBuilder.toString();
        }
        return new RStreamListenerDetector(streamMessageListenerContainer, redisTemplate, consumerGroup, consumerName);
    }

    @Bean
    public RStreamTemplate streamTemplate(RedisTemplate<String, Object> redisTemplate) {
        return new DefaultRStreamTemplate(redisTemplate);
    }
}
