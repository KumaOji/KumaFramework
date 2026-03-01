/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.support.core.exception;

public class CaptchaMismatchException
extends RuntimeException {
    public CaptchaMismatchException() {
    }

    public CaptchaMismatchException(String message) {
        super(message);
    }

    public CaptchaMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public CaptchaMismatchException(Throwable cause) {
        super(cause);
    }

    protected CaptchaMismatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

