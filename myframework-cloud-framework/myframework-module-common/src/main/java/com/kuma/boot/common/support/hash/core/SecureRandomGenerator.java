/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash.core;

import com.kuma.boot.common.support.hash.api.RandomGenerator;
import java.security.SecureRandom;

public class SecureRandomGenerator
implements RandomGenerator {
    protected static final int DEFAULT_NEXT_BYTES_SIZE = 16;
    private int defaultNextBytesSize = 16;
    private SecureRandom secureRandom = new SecureRandom();

    @Override
    public byte[] nextBytes() {
        return this.nextBytes(this.defaultNextBytesSize);
    }

    @Override
    public byte[] nextBytes(int numBytes) {
        if (numBytes <= 0) {
            throw new IllegalArgumentException("numBytes argument must be a positive integer (1 or larger)");
        }
        byte[] bytes = new byte[numBytes];
        this.secureRandom.nextBytes(bytes);
        return bytes;
    }
}

