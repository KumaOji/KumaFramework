/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash.api;

public interface RandomGenerator {
    public byte[] nextBytes();

    public byte[] nextBytes(int var1);
}

