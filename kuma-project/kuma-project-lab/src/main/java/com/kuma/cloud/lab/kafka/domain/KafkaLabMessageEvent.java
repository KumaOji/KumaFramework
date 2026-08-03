package com.kuma.cloud.lab.kafka.domain;

import java.time.Instant;

/**
 * Lab Kafka 消息事件，记录生产或消费的一条消息。
 */
public record KafkaLabMessageEvent(
        String id,
        Direction direction,
        String topic,
        Integer partition,
        Long offset,
        String key,
        String value,
        Instant occurredAt
) {

    public enum Direction {
        PRODUCED,
        CONSUMED
    }
}
