/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.repository.exception;

public class NotConnectedException
extends ConnectionRepositoryException {
    private static final long serialVersionUID = 620L;
    private final String providerId;

    public NotConnectedException(String providerId) {
        super("Not connected to provider '" + providerId + "'");
        this.providerId = providerId;
    }

    public String getProviderId() {
        return this.providerId;
    }
}

