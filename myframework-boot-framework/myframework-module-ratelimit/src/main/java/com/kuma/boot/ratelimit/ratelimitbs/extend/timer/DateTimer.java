/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.extend.timer;

import java.util.Date;

public class DateTimer
implements ITimer {
    private static final DateTimer INSTANCE = new DateTimer();

    public static DateTimer getInstance() {
        return INSTANCE;
    }

    @Override
    public long time() {
        return new Date().getTime();
    }
}

