/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.exception;

public class SessionInvalidException
extends RuntimeException {
    public SessionInvalidException() {
    }

    public SessionInvalidException(String message) {
        super(message);
    }

    public SessionInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionInvalidException(Throwable cause) {
        super(cause);
    }

    public SessionInvalidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

