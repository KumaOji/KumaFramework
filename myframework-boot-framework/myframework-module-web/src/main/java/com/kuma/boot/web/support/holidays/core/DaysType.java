/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonValue
 */
package com.kuma.boot.web.support.holidays.core;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DaysType {
    WEEKDAYS(0),
    REST_DAYS(1),
    HOLIDAYS(2);

    @JsonValue
    private final byte type;

    private DaysType(byte type) {
        this.type = type;
    }

    public static DaysType from(byte type) {
        switch (type) {
            case 0: {
                return WEEKDAYS;
            }
            case 1: {
                return REST_DAYS;
            }
            case 2: {
                return HOLIDAYS;
            }
        }
        throw new IllegalArgumentException("\u672a\u77e5\u7684 DaysType:" + type);
    }

    public byte getType() {
        return this.type;
    }
}

