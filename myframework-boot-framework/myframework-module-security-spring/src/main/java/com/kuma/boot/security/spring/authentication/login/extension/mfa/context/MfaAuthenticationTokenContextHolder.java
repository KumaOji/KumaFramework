/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.login.extension.mfa.context;

public class MfaAuthenticationTokenContextHolder {
    private static final ThreadLocal<MfaTokenContext> holder = new ThreadLocal();

    private MfaAuthenticationTokenContextHolder() {
    }

    public static MfaTokenContext getMfaTokenContext() {
        return holder.get();
    }

    public static void setMfaTokenContext(MfaTokenContext tokenContext) {
        if (tokenContext == null) {
            MfaAuthenticationTokenContextHolder.resetMfaTokenContext();
        } else {
            holder.set(tokenContext);
        }
    }

    public static void resetMfaTokenContext() {
        holder.remove();
    }
}

