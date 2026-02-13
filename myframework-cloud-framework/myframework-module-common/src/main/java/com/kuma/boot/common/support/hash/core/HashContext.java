/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash.core;

import java.nio.charset.Charset;
import java.util.Arrays;

public class HashContext
implements com.kuma.boot.common.support.hash.api.HashContext {
    private byte[] salt;
    private int times;
    private Charset charset;

    public static HashContext newInstance() {
        return new HashContext();
    }

    @Override
    public byte[] salt() {
        return this.salt;
    }

    public HashContext salt(byte[] salt) {
        this.salt = salt;
        return this;
    }

    @Override
    public int times() {
        return this.times;
    }

    public HashContext times(int times) {
        this.times = times;
        return this;
    }

    @Override
    public Charset charset() {
        return this.charset;
    }

    public HashContext charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public String toString() {
        return "HashContext{salt=" + Arrays.toString(this.salt) + ", times=" + this.times + ", charset=" + String.valueOf(this.charset) + "}";
    }
}

