/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitenhance.exception;

public class EnhanceRedisLimitException
extends RuntimeException {
    public EnhanceRedisLimitException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public EnhanceRedisLimitException(String message) {
        super(message);
    }
}

