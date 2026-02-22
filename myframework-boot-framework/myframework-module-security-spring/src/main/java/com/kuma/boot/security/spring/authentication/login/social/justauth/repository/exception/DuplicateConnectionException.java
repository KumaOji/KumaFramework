/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.repository.exception;

import com.kuma.boot.security.spring.authentication.login.social.justauth.entity.ConnectionKey;

public final class DuplicateConnectionException
extends ConnectionRepositoryException {
    private static final long serialVersionUID = 620L;
    private final ConnectionKey connectionKey;

    public DuplicateConnectionException(ConnectionKey connectionKey) {
        super("The connection with key " + String.valueOf(connectionKey) + " already exists");
        this.connectionKey = connectionKey;
    }

    public ConnectionKey getConnectionKey() {
        return this.connectionKey;
    }
}

