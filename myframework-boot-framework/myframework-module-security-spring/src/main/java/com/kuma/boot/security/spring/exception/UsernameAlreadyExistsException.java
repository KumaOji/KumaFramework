/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.exception;

public class UsernameAlreadyExistsException
extends RuntimeException {
    public UsernameAlreadyExistsException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UsernameAlreadyExistsException(String msg) {
        super(msg);
    }
}

