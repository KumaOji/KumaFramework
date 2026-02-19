/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.apache.kafka.common.serialization.StringDeserializer
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
 *  org.springframework.kafka.config.KafkaListenerContainerFactory
 *  org.springframework.kafka.core.ConsumerFactory
 *  org.springframework.kafka.core.DefaultKafkaConsumerFactory
 *  org.springframework.kafka.listener.ConcurrentMessageListenerContainer
 *  org.springframework.kafka.listener.ContainerProperties$AckMode
 *  org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
 */
package com.kuma.boot.mq.kafka.kafkause;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

@Configuration
public class KafkaConsumerConfig {
    @Value(value="${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;
    @Value(value="${spring.kafka.consumer.group-id}")
    private String groupId;
    @Value(value="${spring.kafka.consumer.auto-commit-interval}")
    private String autoCommitInterval;
    @Value(value="${spring.kafka.consumer.enable-auto-commit}")
    private boolean enableAutoCommit;
    @Value(value="${spring.kafka.properties.session.timeout.ms}")
    private String sessionTimeout;
    @Value(value="${spring.kafka.properties.max.poll.interval.ms}")
    private String maxPollIntervalTime;
    @Value(value="${spring.kafka.consumer.max-poll-records}")
    private String maxPollRecords;
    @Value(value="${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;
    @Value(value="${spring.kafka.listener.concurrency}")
    private Integer concurrency;
    @Value(value="${spring.kafka.listener.missing-topics-fatal}")
    private boolean missingTopicsFatal;
    @Value(value="${spring.kafka.listener.poll-timeout}")
    private long pollTimeout;

    @Bean
    public Map<String, Object> consumerConfigs() {
        HashMap<String, Object> propsMap = new HashMap<String, Object>(16);
        propsMap.put("bootstrap.servers", this.bootstrapServers);
        propsMap.put("group.id", this.groupId);
        propsMap.put("enable.auto.commit", this.enableAutoCommit);
        propsMap.put("auto.commit.interval.ms", this.autoCommitInterval);
        propsMap.put("auto.offset.reset", this.autoOffsetReset);
        propsMap.put("max.poll.interval.ms", this.maxPollIntervalTime);
        propsMap.put("max.poll.records", this.maxPollRecords);
        propsMap.put("session.timeout.ms", this.sessionTimeout);
        propsMap.put("key.deserializer", ErrorHandlingDeserializer.class);
        propsMap.put("value.deserializer", ErrorHandlingDeserializer.class);
        propsMap.put("spring.deserializer.key.delegate.class", StringDeserializer.class);
        propsMap.put("spring.deserializer.value.delegate.class", StringDeserializer.class);
        return propsMap;
    }

    @Bean
    public ConsumerFactory<Object, Object> consumerFactory() {
        return new DefaultKafkaConsumerFactory(this.consumerConfigs());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Object, Object>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(this.consumerFactory());
        factory.setConcurrency(this.concurrency);
        factory.setMissingTopicsFatal(this.missingTopicsFatal);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setPollTimeout(this.pollTimeout);
        factory.setBatchListener(Boolean.valueOf(true));
        return factory;
    }
}

