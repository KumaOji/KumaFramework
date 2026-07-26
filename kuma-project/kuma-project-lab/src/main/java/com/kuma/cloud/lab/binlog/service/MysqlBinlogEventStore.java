package com.kuma.cloud.lab.binlog.service;

import com.kuma.cloud.lab.binlog.config.MysqlBinlogProperties;
import com.kuma.cloud.lab.binlog.domain.MysqlBinlogChangeEvent;
import com.kuma.cloud.lab.binlog.domain.MysqlBinlogMonitorStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class MysqlBinlogEventStore {

    private final boolean enabled;
    private final int capacity;
    private final Deque<MysqlBinlogChangeEvent> events = new ArrayDeque<>();
    private final AtomicLong capturedEvents = new AtomicLong();
    private volatile boolean running;
    private volatile Instant lastEventAt;
    private volatile String lastError;

    public MysqlBinlogEventStore(MysqlBinlogProperties properties) {
        this.enabled = properties.enabled();
        this.capacity = properties.bufferCapacity();
    }

    public synchronized void append(MysqlBinlogChangeEvent event) {
        if (events.size() == capacity) {
            events.removeFirst();
        }
        events.addLast(event);
        capturedEvents.incrementAndGet();
        lastEventAt = Instant.now();
    }

    public synchronized List<MysqlBinlogChangeEvent> latest(int requestedLimit) {
        int limit = Math.max(1, Math.min(requestedLimit, capacity));
        List<MysqlBinlogChangeEvent> result = new ArrayList<>(Math.min(limit, events.size()));
        var iterator = events.descendingIterator();
        while (iterator.hasNext() && result.size() < limit) {
            result.add(iterator.next());
        }
        return Collections.unmodifiableList(result);
    }

    public MysqlBinlogMonitorStatus status() {
        return new MysqlBinlogMonitorStatus(
                enabled, running, capturedEvents.get(), lastEventAt, lastError
        );
    }

    public void markRunning(boolean running) {
        this.running = running;
    }

    public void markFailed(Throwable error) {
        this.running = false;
        this.lastError = error == null ? null : error.getMessage();
    }
}
