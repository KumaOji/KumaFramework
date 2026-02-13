/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.async;

public abstract class Clock {
    public abstract long absoluteTimeMillis();

    public abstract long relativeTimeMillis();

    public abstract long relativeTimeNanos();
}

