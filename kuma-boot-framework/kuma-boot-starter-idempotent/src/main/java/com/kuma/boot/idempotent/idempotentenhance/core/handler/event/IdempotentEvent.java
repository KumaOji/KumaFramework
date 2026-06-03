package com.kuma.boot.idempotent.idempotentenhance.core.handler.event;

import java.time.LocalDateTime;

/**
 * 幂等事件
 *
 * @author wenpan 2023/01/07 13:04
 */
public class IdempotentEvent<T> {

    /**
     * 事件来源
     */
    private final String source;
    /**
     * 事件唯一标识
     */
    private final String identifier;
    /**
     * 事件发生事件
     */
    private final LocalDateTime time;
    /**
     * 事件数据
     */
    private final T data;

    public IdempotentEvent(String identifier, String source, T data) {
        this.identifier = identifier;
        this.source = source;
        this.data = data;
        time = LocalDateTime.now();
    }

    public String getSource() {
        return source;
    }

    public String getIdentifier() {
        return identifier;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return "IdempotentEvent{" +
                "source='" + source + '\'' +
                ", identifier='" + identifier + '\'' +
                ", time=" + time +
                ", data=" + data +
                '}';
    }
}
