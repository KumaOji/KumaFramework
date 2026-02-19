/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.annotation.Resource
 *  org.apache.kafka.common.serialization.StringSerializer
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.kafka.core.DefaultKafkaProducerFactory
 *  org.springframework.kafka.core.KafkaTemplate
 *  org.springframework.kafka.core.ProducerFactory
 *  org.springframework.kafka.support.ProducerListener
 *  org.springframework.kafka.support.serializer.JacksonJsonSerializer
 */
package com.kuma.boot.mq.kafka.kafkause;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

@Configuration
public class KafkaProviderConfig {
    @Value(value="${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;
    @Value(value="${spring.kafka.producer.acks}")
    private String acks;
    @Value(value="${spring.kafka.producer.retries}")
    private String retries;
    @Value(value="${spring.kafka.producer.batch-size}")
    private String batchSize;
    @Value(value="${spring.kafka.producer.buffer-memory}")
    private String bufferMemory;
    @Resource
    private KafkaSendResultHandler kafkaSendResultHandler;

    @Bean
    public Map<String, Object> producerConfigs() {
        HashMap<String, Object> props = new HashMap<String, Object>(16);
        props.put("bootstrap.servers", this.bootstrapServers);
        props.put("acks", this.acks);
        props.put("retries", this.retries);
        props.put("batch.size", this.batchSize);
        props.put("linger.ms", "5000");
        props.put("buffer.memory", this.bufferMemory);
        props.put("key.serializer", StringSerializer.class);
        props.put("value.serializer", JacksonJsonSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        DefaultKafkaProducerFactory factory = new DefaultKafkaProducerFactory(this.producerConfigs());
        return factory;
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        KafkaTemplate kafkaTemplate = new KafkaTemplate(this.producerFactory());
        kafkaTemplate.setProducerListener((ProducerListener)this.kafkaSendResultHandler);
        return kafkaTemplate;
    }
}

