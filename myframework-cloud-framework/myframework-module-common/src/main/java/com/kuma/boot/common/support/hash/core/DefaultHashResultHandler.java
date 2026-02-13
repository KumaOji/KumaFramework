/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash.core;

import com.kuma.boot.common.support.hash.api.HashResult;
import com.kuma.boot.common.support.hash.api.HashResultHandler;

public class DefaultHashResultHandler
implements HashResultHandler<HashResult> {
    @Override
    public HashResult handle(HashResult hashResult) {
        return hashResult;
    }
}

