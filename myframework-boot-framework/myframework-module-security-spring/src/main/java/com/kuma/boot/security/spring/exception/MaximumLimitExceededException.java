/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.exception;

public class MaximumLimitExceededException
extends RuntimeException {
    public MaximumLimitExceededException() {
    }

    public MaximumLimitExceededException(String message) {
        super(message);
    }

    public MaximumLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }

    public MaximumLimitExceededException(Throwable cause) {
        super(cause);
    }

    protected MaximumLimitExceededException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

