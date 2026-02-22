/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.core.AuthenticationException
 */
package com.kuma.boot.security.spring.exception;

import org.springframework.security.core.AuthenticationException;

public class ExtensionLoginException
extends AuthenticationException {
    public ExtensionLoginException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ExtensionLoginException(String msg) {
        super(msg);
    }
}

