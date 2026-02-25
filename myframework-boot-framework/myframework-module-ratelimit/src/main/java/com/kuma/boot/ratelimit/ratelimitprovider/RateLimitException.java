/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitprovider;

public class RateLimitException
extends RuntimeException {
    private final long retryAfter;

    public RateLimitException(String message, long retryAfter) {
        super(message);
        this.retryAfter = retryAfter;
    }

    public long getRetryAfter() {
        return this.retryAfter;
    }
}

