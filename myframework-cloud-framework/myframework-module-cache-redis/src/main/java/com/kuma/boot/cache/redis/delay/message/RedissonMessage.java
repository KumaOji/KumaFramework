/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package com.kuma.boot.cache.redis.delay.message;

import java.util.HashMap;
import java.util.Map;
import org.springframework.util.Assert;

public class RedissonMessage {
    private final String payload;
    private final Map<String, Object> headers;

    public RedissonMessage(String payload, Map<String, Object> headers) {
        Assert.notNull((Object)payload, (String)"payload must not be null");
        this.payload = payload;
        if (headers == null) {
            headers = new HashMap<String, Object>();
        }
        this.headers = headers;
    }

    public String getPayload() {
        return this.payload;
    }

    public Map<String, Object> getHeaders() {
        return this.headers;
    }
}

