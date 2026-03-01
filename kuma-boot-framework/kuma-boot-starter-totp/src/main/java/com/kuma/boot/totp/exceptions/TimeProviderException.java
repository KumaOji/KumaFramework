/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.totp.exceptions;

public class TimeProviderException
extends RuntimeException {
    public TimeProviderException(String message) {
        super(message);
    }

    public TimeProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}

