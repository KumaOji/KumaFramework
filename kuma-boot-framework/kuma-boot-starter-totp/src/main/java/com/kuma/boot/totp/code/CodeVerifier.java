/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.totp.code;

public interface CodeVerifier {
    public boolean isValidCode(String var1, String var2);
}

