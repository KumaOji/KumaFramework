package com.kuma.cloud.lab.kafka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Lab Kafka 测试配置。
 */
@ConfigurationProperties(prefix = "kuma.lab.kafka")
public record KafkaLabProperties(
        boolean enabled,
        String bootstrapServers,
        String topic,
        String groupId,
        int bufferCapacity
) {

    public KafkaLabProperties {
        bootstrapServers = hasText(bootstrapServers) ? bootstrapServers : "localhost:9092";
        topic = hasText(topic) ? topic : "kuma-lab-test";
        groupId = hasText(groupId) ? groupId : "kuma-lab-test-group";
        bufferCapacity = bufferCapacity > 0 ? bufferCapacity : 1000;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
