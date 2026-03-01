/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.idempotent.idempotentenhance.core.exception;

public class ConcurrentRequestException
extends RuntimeException {
    public ConcurrentRequestException(String message) {
        super(message);
    }
}

