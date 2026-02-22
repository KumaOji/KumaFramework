/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.exception;

public class StampHasExpiredException
extends RuntimeException {
    public StampHasExpiredException() {
    }

    public StampHasExpiredException(String message) {
        super(message);
    }

    public StampHasExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public StampHasExpiredException(Throwable cause) {
        super(cause);
    }

    protected StampHasExpiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

