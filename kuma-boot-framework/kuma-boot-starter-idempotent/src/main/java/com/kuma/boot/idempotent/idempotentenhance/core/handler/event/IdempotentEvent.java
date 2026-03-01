/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.idempotent.idempotentenhance.core.handler.event;

import java.time.LocalDateTime;

public class IdempotentEvent<T> {
    private final String source;
    private final String identifier;
    private final LocalDateTime time;
    private final T data;

    public IdempotentEvent(String identifier, String source, T data) {
        this.identifier = identifier;
        this.source = source;
        this.data = data;
        this.time = LocalDateTime.now();
    }

    public String getSource() {
        return this.source;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public LocalDateTime getTime() {
        return this.time;
    }

    public T getData() {
        return this.data;
    }

    public String toString() {
        return "IdempotentEvent{source='" + this.source + "', identifier='" + this.identifier + "', time=" + String.valueOf(this.time) + ", data=" + String.valueOf(this.data) + "}";
    }
}

