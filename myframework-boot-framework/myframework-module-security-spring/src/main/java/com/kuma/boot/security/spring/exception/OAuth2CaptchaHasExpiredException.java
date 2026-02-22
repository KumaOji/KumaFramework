/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.exception;

public class OAuth2CaptchaHasExpiredException
extends OAuth2CaptchaException {
    public OAuth2CaptchaHasExpiredException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public OAuth2CaptchaHasExpiredException(String msg) {
        super(msg);
    }
}

