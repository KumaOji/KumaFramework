/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash.core;

import java.util.Arrays;

public class HashResult
implements com.kuma.boot.common.support.hash.api.HashResult {
    private byte[] hashed;

    public static HashResult newInstance() {
        return new HashResult();
    }

    @Override
    public byte[] hashed() {
        return this.hashed;
    }

    public HashResult hashed(byte[] hashed) {
        this.hashed = hashed;
        return this;
    }

    public String toString() {
        return "HashResult{hashed=" + Arrays.toString(this.hashed) + "}";
    }
}

