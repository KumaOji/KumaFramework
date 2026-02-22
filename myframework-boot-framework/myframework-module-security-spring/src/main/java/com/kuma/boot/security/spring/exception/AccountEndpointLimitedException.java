/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.authentication.AccountStatusException
 */
package com.kuma.boot.security.spring.exception;

import org.springframework.security.authentication.AccountStatusException;

public class AccountEndpointLimitedException
extends AccountStatusException {
    public AccountEndpointLimitedException(String msg) {
        super(msg);
    }

    public AccountEndpointLimitedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

