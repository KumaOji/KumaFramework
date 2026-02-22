/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.repository.exception;

public abstract class ConnectionRepositoryException
extends RuntimeException {
    private static final long serialVersionUID = 620L;

    public ConnectionRepositoryException(String message) {
        super(message);
    }

    public ConnectionRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}

