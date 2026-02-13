/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash.core;

import com.kuma.boot.common.support.hash.api.HashContext;
import com.kuma.boot.common.support.hash.core.AbstractHash;

public class EmptyHash
extends AbstractHash {
    private static final byte[] EMPTY = new byte[0];

    @Override
    protected byte[] doHash(byte[] source, HashContext context) {
        return EMPTY;
    }
}

