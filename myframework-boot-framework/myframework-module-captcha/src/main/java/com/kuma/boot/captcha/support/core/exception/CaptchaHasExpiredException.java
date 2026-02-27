/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.support.core.exception;

public class CaptchaHasExpiredException
extends RuntimeException {
    public CaptchaHasExpiredException() {
    }

    public CaptchaHasExpiredException(String message) {
        super(message);
    }

    public CaptchaHasExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public CaptchaHasExpiredException(Throwable cause) {
        super(cause);
    }

    protected CaptchaHasExpiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

