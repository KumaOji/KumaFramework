/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash.core;

public class HashRuntimeException
extends RuntimeException {
    public HashRuntimeException() {
    }

    public HashRuntimeException(String message) {
        super(message);
    }

    public HashRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public HashRuntimeException(Throwable cause) {
        super(cause);
    }

    public HashRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

