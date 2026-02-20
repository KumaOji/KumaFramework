/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 */
package com.kuma.boot.web.support.sse;

import org.springframework.context.ApplicationEvent;

public class SseConnectionEvent
extends ApplicationEvent {
    private String id;

    public SseConnectionEvent(Object source) {
        super(source);
        this.id = source.toString();
    }

    public String getId() {
        return this.id;
    }
}

