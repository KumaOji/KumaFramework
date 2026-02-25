/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.core.exception;

public class RateLimitRuntimeException
extends RuntimeException {
    private static final long serialVersionUID = -7828188205076249256L;

    public RateLimitRuntimeException() {
    }

    public RateLimitRuntimeException(String message) {
        super(message);
    }

    public RateLimitRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public RateLimitRuntimeException(Throwable cause) {
        super(cause);
    }

    public RateLimitRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

