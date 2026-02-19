/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.annotation.Resource
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.kafka.core.KafkaTemplate
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.mq.kafka.kafkause;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventProvider {
    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Value(value="${slr-connector.kafka.provider_topic}")
    private String providerTopic;
}

