/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.repository.exception;

import com.kuma.boot.security.spring.authentication.login.social.justauth.entity.ConnectionKey;

public final class NoSuchConnectionException
extends ConnectionRepositoryException {
    private static final long serialVersionUID = 620L;
    private final ConnectionKey connectionKey;

    public NoSuchConnectionException(ConnectionKey connectionKey) {
        super("No such connection exists with key " + String.valueOf(connectionKey));
        this.connectionKey = connectionKey;
    }

    public ConnectionKey getConnectionKey() {
        return this.connectionKey;
    }
}

