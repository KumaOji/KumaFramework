/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash.core;

import com.kuma.boot.common.support.hash.api.HashResult;
import com.kuma.boot.common.support.hash.api.HashResultHandler;

public class BytesHashResultHandler
implements HashResultHandler<byte[]> {
    @Override
    public byte[] handle(HashResult hashResult) {
        return hashResult.hashed();
    }
}

