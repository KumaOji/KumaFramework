/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.entity;

import java.io.Serializable;

public final class ConnectionKey
implements Serializable {
    private static final long serialVersionUID = 620L;
    private final String providerId;
    private final String providerUserId;

    public ConnectionKey(String providerId, String providerUserId) {
        this.providerId = providerId;
        this.providerUserId = providerUserId;
    }

    public String getProviderId() {
        return this.providerId;
    }

    public String getProviderUserId() {
        return this.providerUserId;
    }

    public boolean equals(Object o) {
        if (!(o instanceof ConnectionKey)) {
            return false;
        }
        ConnectionKey other = (ConnectionKey)o;
        boolean sameProvider = this.providerId.equals(other.providerId);
        return this.providerUserId != null ? sameProvider && this.providerUserId.equals(other.providerUserId) : sameProvider && other.providerUserId == null;
    }

    public int hashCode() {
        int hashCode = this.providerId.hashCode();
        return this.providerUserId != null ? hashCode + this.providerUserId.hashCode() : hashCode;
    }

    public String toString() {
        return this.providerId + ":" + this.providerUserId;
    }
}

