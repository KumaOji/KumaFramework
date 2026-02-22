/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.authentication.AccountStatusException
 */
package com.kuma.boot.security.spring.exception;

import org.springframework.security.authentication.AccountStatusException;

public class OAuth2CaptchaException
extends AccountStatusException {
    public OAuth2CaptchaException(String msg) {
        super(msg);
    }

    public OAuth2CaptchaException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

