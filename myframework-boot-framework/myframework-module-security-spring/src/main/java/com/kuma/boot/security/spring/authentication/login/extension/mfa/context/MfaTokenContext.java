/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.login.extension.mfa.context;

public final class MfaTokenContext {
    private final boolean mfa;

    public MfaTokenContext(boolean mfa) {
        this.mfa = mfa;
    }

    public boolean isMfa() {
        return this.mfa;
    }
}

