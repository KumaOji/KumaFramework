/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash.api;

import com.kuma.boot.common.support.hash.api.HashContext;
import com.kuma.boot.common.support.hash.api.HashResult;

public interface Hash {
    public HashResult hash(byte[] var1, HashContext var2);
}

