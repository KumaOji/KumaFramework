/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.support.core.exception;

public class CaptchaIsEmptyException
extends RuntimeException {
    public CaptchaIsEmptyException() {
    }

    public CaptchaIsEmptyException(String message) {
        super(message);
    }

    public CaptchaIsEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public CaptchaIsEmptyException(Throwable cause) {
        super(cause);
    }

    protected CaptchaIsEmptyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

