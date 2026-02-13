/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.async;

import com.kuma.boot.common.utils.async.Clock;

public final class SystemClock
extends Clock {
    private static final SystemClock INSTANCE = new SystemClock();

    public static SystemClock getInstance() {
        return INSTANCE;
    }

    @Override
    public long absoluteTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public long relativeTimeMillis() {
        return System.nanoTime() / 1000000L;
    }

    @Override
    public long relativeTimeNanos() {
        return System.nanoTime();
    }

    private SystemClock() {
    }
}

