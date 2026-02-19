/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package com.kuma.boot.cache.redis.delay.message;

import java.util.Map;
import org.springframework.util.Assert;

public class QueueMessage<T> {
    private final T payload;
    private final Map<String, Object> headers;

    public QueueMessage(T payload, Map<String, Object> headers) {
        Assert.notNull(payload, (String)"payload must not be null");
        this.payload = payload;
        this.headers = headers;
    }

    public T getPayload() {
        return this.payload;
    }

    public Map<String, Object> getHeaders() {
        return this.headers;
    }
}

