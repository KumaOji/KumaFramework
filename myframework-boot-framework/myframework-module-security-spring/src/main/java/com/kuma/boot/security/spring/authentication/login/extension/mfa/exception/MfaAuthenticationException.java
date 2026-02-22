/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.core.AuthenticationException
 */
package com.kuma.boot.security.spring.authentication.login.extension.mfa.exception;

import org.springframework.security.core.AuthenticationException;

public class MfaAuthenticationException
extends AuthenticationException {
    public MfaAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

