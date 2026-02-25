/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.lock.exception;

public class UnSupportLockException
extends RuntimeException {
    public UnSupportLockException(String message) {
        super(message);
    }

    public UnSupportLockException(String message, Throwable cause) {
        super(message, cause);
    }
}

