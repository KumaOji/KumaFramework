/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.secret.api;

import com.kuma.boot.common.support.secret.api.SecretContext;

public interface Secret {
    public byte[] encrypt(SecretContext var1);

    public byte[] decrypt(SecretContext var1);
}

