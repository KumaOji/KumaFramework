/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.support.holidays.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public interface HolidaysApi {
    public DaysType getDaysType(LocalDate var1);

    default public DaysType getDaysType(LocalDateTime localDateTime) {
        return this.getDaysType(localDateTime.toLocalDate());
    }

    default public DaysType getDaysType(Date date) {
        return this.getDaysType(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    default public boolean isWeekdays(LocalDate localDate) {
        return DaysType.WEEKDAYS.equals((Object)this.getDaysType(localDate));
    }

    default public boolean isWeekdays(LocalDateTime localDateTime) {
        return DaysType.WEEKDAYS.equals((Object)this.getDaysType(localDateTime));
    }

    default public boolean isWeekdays(Date date) {
        return DaysType.WEEKDAYS.equals((Object)this.getDaysType(date));
    }
}

