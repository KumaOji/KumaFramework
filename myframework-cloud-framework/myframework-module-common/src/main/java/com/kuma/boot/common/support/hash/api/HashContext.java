/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash.api;

import java.nio.charset.Charset;

public interface HashContext {
    public byte[] salt();

    public int times();

    public Charset charset();
}

