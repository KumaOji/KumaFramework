/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.login.extension.mfa.totp;

public interface MfaAuthenticationManager {
    public String generateSecret();

    public boolean validCode(String var1, String var2);

    public String getUriForImage(String var1, String var2, String var3) throws Exception;
}

