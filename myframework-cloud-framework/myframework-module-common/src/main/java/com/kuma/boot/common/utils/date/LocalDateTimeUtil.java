/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.date;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class LocalDateTimeUtil {
    public static boolean between(LocalDateTime now, LocalDateTime start, LocalDateTime end) {
        return LocalDateTimeUtil.ge(now, start) && LocalDateTimeUtil.le(now, end);
    }

    public static boolean gt(LocalDateTime now, LocalDateTime next) {
        long epochMilli;
        long mills = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return mills > (epochMilli = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    public static boolean lt(LocalDateTime now, LocalDateTime next) {
        long epochMilli;
        long mills = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return mills < (epochMilli = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    public static boolean ge(LocalDateTime now, LocalDateTime next) {
        long epochMilli;
        long mills = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return mills >= (epochMilli = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    public static boolean le(LocalDateTime now, LocalDateTime next) {
        long epochMilli;
        long mills = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return mills <= (epochMilli = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    public static LocalDateTime date2DateTime(LocalDate localDate) {
        return localDate.atTime(0, 0);
    }

    public static LocalDateTime parse(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    public static long timestamp(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.toEpochMilli();
    }
}

