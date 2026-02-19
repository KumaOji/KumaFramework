/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.request.altas.exception;

public class LogException
extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public LogException() {
    }

    public LogException(String message) {
        super(message);
    }

    public LogException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogException(Throwable cause) {
        super(cause);
    }
}

