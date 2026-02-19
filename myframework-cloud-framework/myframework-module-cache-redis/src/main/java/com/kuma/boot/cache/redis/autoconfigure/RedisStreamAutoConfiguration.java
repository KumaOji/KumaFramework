/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.constant.CommonConstants
 *  com.kuma.boot.common.utils.io.NetUtils
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.redisson.spring.data.connection.RedissonConnectionFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.ObjectProvider
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.web.server.autoconfigure.ServerProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.core.env.Environment
 *  org.springframework.data.redis.connection.RedisConnectionFactory
 *  org.springframework.data.redis.connection.stream.MapRecord
 *  org.springframework.data.redis.core.RedisTemplate
 *  org.springframework.data.redis.serializer.RedisSerializer
 *  org.springframework.data.redis.stream.StreamMessageListenerContainer
 *  org.springframework.data.redis.stream.StreamMessageListenerContainer$StreamMessageListenerContainerOptions
 *  org.springframework.data.redis.stream.StreamMessageListenerContainer$StreamMessageListenerContainerOptionsBuilder
 *  org.springframework.util.ErrorHandler
 */
package com.kuma.boot.cache.redis.autoconfigure;

import com.kuma.boot.cache.redis.autoconfigure.properties.CacheManagerProperties;
import com.kuma.boot.cache.redis.stream.DefaultRStreamTemplate;
import com.kuma.boot.cache.redis.stream.RStreamListenerDetector;
import com.kuma.boot.cache.redis.stream.RStreamTemplate;
import com.kuma.boot.common.constant.CommonConstants;
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
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.util.ErrorHandler;

@AutoConfiguration
@ConditionalOnProperty(prefix="kuma.boot.cache.redis.cache-manager.stream", name={"enable"}, havingValue="true")
public class RedisStreamAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(RedisStreamAutoConfiguration.class, (String)"kuma-boot-starter-cache-redis", (String[])new String[0]);
    }

    @Bean
    @ConditionalOnMissingBean
    public StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, byte[]>> streamMessageListenerContainerOptions(CacheManagerProperties properties, ObjectProvider<ErrorHandler> errorHandlerObjectProvider) {
        Duration pollTimeout;
        StreamMessageListenerContainer.StreamMessageListenerContainerOptionsBuilder builder = StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder().keySerializer(RedisSerializer.string()).hashKeySerializer(RedisSerializer.string()).hashValueSerializer(RedisSerializer.byteArray());
        CacheManagerProperties.Stream streamProperties = properties.getStream();
        Integer pollBatchSize = streamProperties.getPollBatchSize();
        if (pollBatchSize != null && pollBatchSize > 0) {
            builder.batchSize(pollBatchSize.intValue());
        }
        if ((pollTimeout = streamProperties.getPollTimeout()) != null && !pollTimeout.isNegative()) {
            builder.pollTimeout(pollTimeout);
        }
        errorHandlerObjectProvider.ifAvailable(arg_0 -> ((StreamMessageListenerContainer.StreamMessageListenerContainerOptionsBuilder)builder).errorHandler(arg_0));
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public StreamMessageListenerContainer<String, MapRecord<String, String, byte[]>> streamMessageListenerContainer(RedissonConnectionFactory redisConnectionFactory, StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, byte[]>> streamMessageListenerContainerOptions) {
        return StreamMessageListenerContainer.create((RedisConnectionFactory)redisConnectionFactory, streamMessageListenerContainerOptions);
    }

    @Bean
    @ConditionalOnMissingBean
    public RStreamListenerDetector streamListenerDetector(StreamMessageListenerContainer<String, MapRecord<String, String, byte[]>> streamMessageListenerContainer, RedisTemplate<String, Object> redisTemplate, ObjectProvider<ServerProperties> serverPropertiesObjectProvider, CacheManagerProperties properties, Environment environment) {
        String consumerName;
        CacheManagerProperties.Stream streamProperties = properties.getStream();
        Object consumerGroup = streamProperties.getConsumerGroup();
        if (StringUtils.isBlank((String)consumerGroup)) {
            String appName = environment.getRequiredProperty(CommonConstants.SPRING_APP_NAME_KEY);
            String profile = environment.getProperty(CommonConstants.ACTIVE_PROFILES_PROPERTY);
            Object object = consumerGroup = StringUtils.isBlank((String)profile) ? appName + ":" : appName + ":" + profile;
        }
        if (StringUtils.isBlank((String)(consumerName = streamProperties.getConsumerName()))) {
            StringBuilder consumerNameBuilder = new StringBuilder(NetUtils.getHostIp());
            serverPropertiesObjectProvider.ifAvailable(serverProperties -> consumerNameBuilder.append(':').append(serverProperties.getPort()));
            consumerName = consumerNameBuilder.toString();
        }
        return new RStreamListenerDetector(streamMessageListenerContainer, redisTemplate, (String)consumerGroup, consumerName);
    }

    @Bean
    public RStreamTemplate streamTemplate(RedisTemplate<String, Object> redisTemplate) {
        return new DefaultRStreamTemplate(redisTemplate);
    }
}

