/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.extend.timer;

public final class TimerHelper {
    private TimerHelper() {
    }

    public static long system() {
        return Timers.system().time();
    }

    public static long date() {
        return Timers.date().time();
    }

    public static long calendar() {
        return Timers.calendar().time();
    }

    public static long scheduled() {
        return Timers.scheduled().time();
    }
}

