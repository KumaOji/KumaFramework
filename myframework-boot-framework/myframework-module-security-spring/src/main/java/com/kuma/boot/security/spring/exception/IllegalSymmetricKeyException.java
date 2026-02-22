/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.exception;

public class IllegalSymmetricKeyException
extends PlatformAuthenticationException {
    public IllegalSymmetricKeyException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public IllegalSymmetricKeyException(String msg) {
        super(msg);
    }
}

