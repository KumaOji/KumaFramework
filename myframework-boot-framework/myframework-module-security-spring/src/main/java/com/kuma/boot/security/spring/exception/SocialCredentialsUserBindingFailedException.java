/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.exception;

public class SocialCredentialsUserBindingFailedException
extends PlatformAuthenticationException {
    public SocialCredentialsUserBindingFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SocialCredentialsUserBindingFailedException(String msg) {
        super(msg);
    }
}

