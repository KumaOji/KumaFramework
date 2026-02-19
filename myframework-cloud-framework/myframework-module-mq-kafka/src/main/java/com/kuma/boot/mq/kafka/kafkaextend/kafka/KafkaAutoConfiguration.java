/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Scope
 */
package com.kuma.boot.mq.kafka.kafkaextend.kafka;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@AutoConfiguration
@EnableConfigurationProperties(value={KafkaProperties.class})
public class KafkaAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(value={KafkaExtendProducer.class})
    public KafkaExtendProducer<String, String> stringKafkaExtendProducer(KafkaProperties properties) {
        KafkaProducerBuilder builder = new KafkaProducerBuilder().addAllBootstrapServers(properties.getBootstrapServers()).putAll(properties.getExtend());
        builder.keySerializer(properties.getKeySerializerClassName());
        builder.valueSerializer(properties.getValueSerializerClassName());
        return builder.build();
    }

    @Bean
    @Scope(value="prototype")
    @ConditionalOnMissingBean(value={KafkaConsumerBuilder.class})
    public KafkaConsumerBuilder consumerBuilder(KafkaProperties properties) {
        return new KafkaConsumerBuilder().addAllBootstrapServers(properties.getBootstrapServers()).keyDeserializer(properties.getKeyDeserializerClassName()).valueDeserializer(properties.getValueDeserializerClassName()).groupId(properties.getGroupId());
    }
}

