package com.kuma.cloud.lab.kafka.domain.vo;

/**
 * Kafka 发送测试结果。
 */
public record KafkaLabSendVO(
        String eventId,
        String topic,
        Integer partition,
        Long offset
) {
}
