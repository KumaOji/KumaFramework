/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.extend.timer;

public final class Timers {
    private Timers() {
    }

    public static ITimer system() {
        return SystemTimer.getInstance();
    }

    public static ITimer date() {
        return DateTimer.getInstance();
    }

    public static ITimer calendar() {
        return CalendarTimer.getInstance();
    }

    public static ITimer scheduled() {
        return ScheduledTimer.getInstance();
    }
}

