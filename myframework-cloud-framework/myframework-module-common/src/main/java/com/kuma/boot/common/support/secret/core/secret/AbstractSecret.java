/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.secret.core.secret;

import com.kuma.boot.common.support.secret.api.Secret;
import com.kuma.boot.common.support.secret.api.SecretContext;

public abstract class AbstractSecret
implements Secret {
    protected abstract byte[] doEncrypt(SecretContext var1);

    protected abstract byte[] doDecrypt(SecretContext var1);

    @Override
    public byte[] encrypt(SecretContext context) {
        byte[] source = context.source();
        return source == null ? null : this.doEncrypt(context);
    }

    @Override
    public byte[] decrypt(SecretContext context) {
        byte[] source = context.source();
        return source == null ? null : this.doDecrypt(context);
    }
}

