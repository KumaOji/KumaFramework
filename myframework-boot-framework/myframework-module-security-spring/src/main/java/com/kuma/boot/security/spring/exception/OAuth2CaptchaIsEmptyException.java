/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.exception;

public class OAuth2CaptchaIsEmptyException
extends OAuth2CaptchaException {
    public OAuth2CaptchaIsEmptyException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public OAuth2CaptchaIsEmptyException(String msg) {
        super(msg);
    }
}

