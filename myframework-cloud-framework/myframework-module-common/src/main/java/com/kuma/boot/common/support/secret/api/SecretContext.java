/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.secret.api;

public interface SecretContext {
    public SecretContext source(byte[] var1);

    public byte[] source();

    public String sourceText();

    public SecretContext key(byte[] var1);

    public byte[] key();

    public String keyText();

    public SecretContext charset(String var1);

    public String charset();
}

