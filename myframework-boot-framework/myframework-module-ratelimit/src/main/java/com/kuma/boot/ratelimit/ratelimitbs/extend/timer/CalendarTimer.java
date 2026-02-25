/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.extend.timer;

import java.util.Calendar;

public class CalendarTimer
implements ITimer {
    private static final CalendarTimer INSTANCE = new CalendarTimer();

    public static CalendarTimer getInstance() {
        return INSTANCE;
    }

    @Override
    public long time() {
        return Calendar.getInstance().getTimeInMillis();
    }
}

