/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.authentication.AccountStatusException
 */
package com.kuma.boot.security.spring.exception;

import org.springframework.security.authentication.AccountStatusException;

public class SessionExpiredException
extends AccountStatusException {
    public SessionExpiredException(String msg) {
        super(msg);
    }

    public SessionExpiredException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

