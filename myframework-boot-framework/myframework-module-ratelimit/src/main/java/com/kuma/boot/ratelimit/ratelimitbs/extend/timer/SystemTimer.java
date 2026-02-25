/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.extend.timer;

public class SystemTimer
implements ITimer {
    private static final SystemTimer INSTANCE = new SystemTimer();

    public static SystemTimer getInstance() {
        return INSTANCE;
    }

    @Override
    public long time() {
        return System.currentTimeMillis();
    }
}

