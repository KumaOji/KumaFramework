/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash.core;

import com.kuma.boot.common.support.hash.core.AbstractHashCode;

public class HashCodeFnv
extends AbstractHashCode {
    private static final long FNV_32_INIT = 2166136261L;
    private static final int FNV_32_PRIME = 16777619;

    @Override
    public int doHash(String text) {
        int hash = -2128831035;
        for (int i = 0; i < text.length(); ++i) {
            hash = (hash ^ text.charAt(i)) * 16777619;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        hash = Math.abs(hash);
        return hash;
    }
}

