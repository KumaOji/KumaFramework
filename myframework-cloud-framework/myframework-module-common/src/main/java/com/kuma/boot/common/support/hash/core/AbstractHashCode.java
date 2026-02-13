/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash.core;

import com.kuma.boot.common.support.hash.api.HashCode;

public abstract class AbstractHashCode
implements HashCode {
    @Override
    public int hash(String text) {
        return null == text ? 0 : this.doHash(text);
    }

    protected abstract int doHash(String var1);
}

