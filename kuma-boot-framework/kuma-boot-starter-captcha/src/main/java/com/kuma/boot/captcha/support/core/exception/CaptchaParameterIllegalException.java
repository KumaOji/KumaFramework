/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.support.core.exception;

public class CaptchaParameterIllegalException
extends RuntimeException {
    public CaptchaParameterIllegalException() {
    }

    public CaptchaParameterIllegalException(String message) {
        super(message);
    }

    public CaptchaParameterIllegalException(String message, Throwable cause) {
        super(message, cause);
    }

    public CaptchaParameterIllegalException(Throwable cause) {
        super(cause);
    }

    protected CaptchaParameterIllegalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

