/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 */
package com.kuma.boot.security.spring.event;

import java.time.Clock;
import org.springframework.context.ApplicationEvent;

public class LocalApplicationEvent<T>
extends ApplicationEvent {
    private final T data;

    public LocalApplicationEvent(T data) {
        super(data);
        this.data = data;
    }

    public LocalApplicationEvent(T data, Clock clock) {
        super(data, clock);
        this.data = data;
    }

    public T getData() {
        return this.data;
    }
}

