/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitredis;

import java.util.concurrent.TimeUnit;

public class RateLimiterException
extends RuntimeException {
    private final String key;
    private final long max;
    private final long ttl;
    private final TimeUnit timeUnit;

    public RateLimiterException(String key, long max, long ttl, TimeUnit timeUnit) {
        super(String.format("\u60a8\u7684\u8bbf\u95ee\u6b21\u6570\u5df2\u8d85\u9650\uff1a%s\uff0c\u901f\u7387\uff1a%d/%ds", key, max, timeUnit.toSeconds(ttl)));
        this.key = key;
        this.max = max;
        this.ttl = ttl;
        this.timeUnit = timeUnit;
    }

    public String getKey() {
        return this.key;
    }

    public long getMax() {
        return this.max;
    }

    public long getTtl() {
        return this.ttl;
    }

    public TimeUnit getTimeUnit() {
        return this.timeUnit;
    }
}

