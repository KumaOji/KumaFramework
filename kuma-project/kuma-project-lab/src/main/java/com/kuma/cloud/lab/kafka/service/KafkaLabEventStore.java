package com.kuma.cloud.lab.kafka.service;

import com.kuma.cloud.lab.kafka.config.KafkaLabProperties;
import com.kuma.cloud.lab.kafka.domain.KafkaLabMessageEvent;
import com.kuma.cloud.lab.kafka.domain.KafkaLabMonitorStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class KafkaLabEventStore {

    private final boolean enabled;
    private final String topic;
    private final String groupId;
    private final String bootstrapServers;
    private final int capacity;
    private final Deque<KafkaLabMessageEvent> events = new ArrayDeque<>();
    private final AtomicLong producedCount = new AtomicLong();
    private final AtomicLong consumedCount = new AtomicLong();
    private volatile boolean listening;
    private volatile Instant lastProducedAt;
    private volatile Instant lastConsumedAt;
    private volatile String lastError;

    public KafkaLabEventStore(KafkaLabProperties properties) {
        this.enabled = properties.enabled();
        this.topic = properties.topic();
        this.groupId = properties.groupId();
        this.bootstrapServers = properties.bootstrapServers();
        this.capacity = properties.bufferCapacity();
    }

    public synchronized KafkaLabMessageEvent appendProduced(
            String topic,
            Integer partition,
            Long offset,
            String key,
            String value
    ) {
        KafkaLabMessageEvent event = new KafkaLabMessageEvent(
                UUID.randomUUID().toString(),
                KafkaLabMessageEvent.Direction.PRODUCED,
                topic,
                partition,
                offset,
                key,
                value,
                Instant.now()
        );
        append(event);
        producedCount.incrementAndGet();
        lastProducedAt = event.occurredAt();
        return event;
    }

    public synchronized void appendConsumed(
            String topic,
            Integer partition,
            Long offset,
            String key,
            String value
    ) {
        KafkaLabMessageEvent event = new KafkaLabMessageEvent(
                UUID.randomUUID().toString(),
                KafkaLabMessageEvent.Direction.CONSUMED,
                topic,
                partition,
                offset,
                key,
                value,
                Instant.now()
        );
        append(event);
        consumedCount.incrementAndGet();
        lastConsumedAt = event.occurredAt();
    }

    private void append(KafkaLabMessageEvent event) {
        if (events.size() == capacity) {
            events.removeFirst();
        }
        events.addLast(event);
    }

    public synchronized List<KafkaLabMessageEvent> latest(int requestedLimit, KafkaLabMessageEvent.Direction direction) {
        int limit = Math.max(1, Math.min(requestedLimit, capacity));
        List<KafkaLabMessageEvent> result = new ArrayList<>();
        var iterator = events.descendingIterator();
        while (iterator.hasNext() && result.size() < limit) {
            KafkaLabMessageEvent event = iterator.next();
            if (direction == null || event.direction() == direction) {
                result.add(event);
            }
        }
        return Collections.unmodifiableList(result);
    }

    public KafkaLabMonitorStatus status() {
        return new KafkaLabMonitorStatus(
                enabled,
                listening,
                producedCount.get(),
                consumedCount.get(),
                lastProducedAt,
                lastConsumedAt,
                lastError,
                topic,
                groupId,
                bootstrapServers
        );
    }

    public void markListening(boolean listening) {
        this.listening = listening;
    }

    public void markFailed(Throwable error) {
        this.listening = false;
        this.lastError = error == null ? null : error.getMessage();
    }
}
