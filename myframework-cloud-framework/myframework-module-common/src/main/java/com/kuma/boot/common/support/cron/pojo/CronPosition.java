/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.cron.pojo;

public enum CronPosition {
    SECOND(0, 59),
    MINUTE(0, 59),
    HOUR(0, 23),
    DAY(1, 31),
    MONTH(1, 12),
    WEEK(0, 6),
    YEAR(2018, 2099);

    private int min;
    private int max;

    private CronPosition(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    public static CronPosition fromPosition(int position) {
        for (CronPosition cronPosition : CronPosition.values()) {
            if (position != cronPosition.ordinal()) continue;
            return cronPosition;
        }
        return null;
    }
}

