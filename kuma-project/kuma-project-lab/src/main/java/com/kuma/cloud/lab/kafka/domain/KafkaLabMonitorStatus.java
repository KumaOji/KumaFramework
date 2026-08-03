package com.kuma.cloud.lab.kafka.domain;

import java.time.Instant;

/**
 * Lab Kafka 监听器运行状态。
 */
public record KafkaLabMonitorStatus(
        boolean enabled,
        boolean listening,
        long producedCount,
        long consumedCount,
        Instant lastProducedAt,
        Instant lastConsumedAt,
        String lastError,
        String topic,
        String groupId,
        String bootstrapServers
) {
}
