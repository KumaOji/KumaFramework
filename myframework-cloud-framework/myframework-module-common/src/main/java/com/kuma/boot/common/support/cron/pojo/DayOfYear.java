/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.cron.pojo;

import java.util.Calendar;

public final class DayOfYear
implements Comparable<DayOfYear> {
    private int day;
    private int month;
    private int year;

    public DayOfYear(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay() {
        return this.day;
    }

    public int getMonth() {
        return this.month;
    }

    public int getYear() {
        return this.year;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DayOfYear dayOfYear = (DayOfYear)o;
        if (this.day != dayOfYear.day) {
            return false;
        }
        if (this.month != dayOfYear.month) {
            return false;
        }
        return this.year == dayOfYear.year;
    }

    public int hashCode() {
        int result = this.day;
        result = 31 * result + this.month;
        result = 31 * result + this.year;
        return result;
    }

    public int week() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1, this.getYear());
        calendar.set(5, this.getDay());
        calendar.set(2, this.getMonth() - 1);
        return calendar.get(7) - 1;
    }

    @Override
    public int compareTo(DayOfYear o) {
        if (this.getYear() > o.getYear()) {
            return 1;
        }
        if (this.getYear() < o.getYear()) {
            return -1;
        }
        if (this.getMonth() > o.getMonth()) {
            return 1;
        }
        if (this.getMonth() < o.getMonth()) {
            return -1;
        }
        if (this.getDay() > o.getDay()) {
            return 1;
        }
        if (this.getDay() < o.getDay()) {
            return -1;
        }
        return 0;
    }
}

