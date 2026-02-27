/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.utils;

import java.util.Random;

public final class JVMRandom
extends Random {
    private static final long serialVersionUID = 1L;
    private boolean constructed = true;

    @Override
    public synchronized void setSeed(long seed) {
        if (this.constructed) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public synchronized double nextGaussian() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void nextBytes(byte[] byteArray) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int nextInt() {
        return this.nextInt(Integer.MAX_VALUE);
    }

    @Override
    public int nextInt(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Upper bound for nextInt must be positive");
        }
        return (int)(Math.random() * (double)n);
    }

    @Override
    public long nextLong() {
        return this.nextLong(Long.MAX_VALUE);
    }

    @Override
    public long nextLong(long n) {
        if (n <= 0L) {
            throw new IllegalArgumentException("Upper bound for nextInt must be positive");
        }
        return (long)(Math.random() * (double)n);
    }

    @Override
    public boolean nextBoolean() {
        return Math.random() > 0.5;
    }

    @Override
    public float nextFloat() {
        return (float)Math.random();
    }

    @Override
    public double nextDouble() {
        return Math.random();
    }
}

