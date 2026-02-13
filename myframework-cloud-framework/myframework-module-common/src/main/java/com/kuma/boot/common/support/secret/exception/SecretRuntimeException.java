/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.secret.exception;

public class SecretRuntimeException
extends RuntimeException {
    public SecretRuntimeException() {
    }

    public SecretRuntimeException(String message) {
        super(message);
    }

    public SecretRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SecretRuntimeException(Throwable cause) {
        super(cause);
    }

    public SecretRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

