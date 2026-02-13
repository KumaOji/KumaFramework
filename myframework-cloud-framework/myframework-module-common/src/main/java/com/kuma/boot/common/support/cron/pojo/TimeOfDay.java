/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.cron.pojo;

import com.kuma.boot.common.support.cron.util.DateUtil;

public final class TimeOfDay
implements Comparable<TimeOfDay> {
    private int hour;
    private int minute;
    private int second;

    public TimeOfDay(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public int getHour() {
        return this.hour;
    }

    public int getMinute() {
        return this.minute;
    }

    public int getSecond() {
        return this.second;
    }

    public String toString() {
        return "TimeOfDay{hour=" + this.hour + ", minute=" + this.minute + ", second=" + this.second + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TimeOfDay ofDay = (TimeOfDay)o;
        if (this.hour != ofDay.hour) {
            return false;
        }
        if (this.minute != ofDay.minute) {
            return false;
        }
        return this.second == ofDay.second;
    }

    public int hashCode() {
        int result = this.hour;
        result = 31 * result + this.minute;
        result = 31 * result + this.second;
        return result;
    }

    @Override
    public int compareTo(TimeOfDay o) {
        if (this.getHour() > o.getHour()) {
            return 1;
        }
        if (this.getHour() < o.getHour()) {
            return -1;
        }
        if (this.getMinute() > o.getMinute()) {
            return 1;
        }
        if (this.getMinute() < o.getMinute()) {
            return -1;
        }
        if (this.getSecond() > o.getSecond()) {
            return 1;
        }
        if (this.getSecond() < o.getSecond()) {
            return -1;
        }
        return 0;
    }

    public boolean equalsWithTolerance(TimeOfDay another, int seconds) {
        return DateUtil.equalsWithTolerance(this, another, seconds);
    }
}

