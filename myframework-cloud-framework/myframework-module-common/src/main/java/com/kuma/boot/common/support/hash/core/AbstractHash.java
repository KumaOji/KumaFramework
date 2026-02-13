/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash.core;

import com.kuma.boot.common.support.hash.api.Hash;
import com.kuma.boot.common.support.hash.api.HashContext;
import com.kuma.boot.common.support.hash.core.HashResult;

public abstract class AbstractHash
implements Hash {
    protected abstract byte[] doHash(byte[] var1, HashContext var2);

    @Override
    public com.kuma.boot.common.support.hash.api.HashResult hash(byte[] source, HashContext context) {
        byte[] hashed = this.doHash(source, context);
        return HashResult.newInstance().hashed(hashed);
    }
}

