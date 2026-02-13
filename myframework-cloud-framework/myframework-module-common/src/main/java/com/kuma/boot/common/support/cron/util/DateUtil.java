/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.cron.util;

import com.kuma.boot.common.support.cron.pojo.TimeOfDay;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static final String SDF_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String SDF_DATETIME_SHORT = "yyyyMMddHHmmss";
    public static final String SDF_DATETIME_MS = "yyyyMMddHHmmssSSS";
    public static final String SDF_DATE = "yyyy-MM-dd";

    public static Date toDate(String dateStr) {
        return DateUtil.toDate(dateStr, null);
    }

    public static String toStr(Date date) {
        return DateUtil.toStr(date, SDF_DATETIME);
    }

    public static String toStr(Date date, String format) {
        if (null != format && !"".equals(format)) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        }
        SimpleDateFormat sdf = new SimpleDateFormat(SDF_DATETIME);
        return sdf.format(date);
    }

    public static Date toDate(String dateStr, String pattern) {
        try {
            if (null != pattern && !"".equals(pattern)) {
                return new SimpleDateFormat(pattern).parse(dateStr);
            }
            return new SimpleDateFormat(SDF_DATETIME).parse(dateStr);
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static int day(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return DateUtil.day(cal);
    }

    public static int day(Calendar calendar) {
        return calendar.get(5);
    }

    public static int week(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return DateUtil.week(cal);
    }

    public static int week(Calendar calendar) {
        return calendar.get(7) - 1;
    }

    public static int month(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return DateUtil.month(cal);
    }

    public static int month(Calendar calendar) {
        return calendar.get(2) + 1;
    }

    public static int year(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return DateUtil.year(cal);
    }

    public static int year(Calendar calendar) {
        return calendar.get(1);
    }

    public static int hour(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return DateUtil.hour(cal);
    }

    public static int hour(Calendar calendar) {
        return calendar.get(11);
    }

    public static int minute(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return DateUtil.minute(cal);
    }

    public static int minute(Calendar calendar) {
        return calendar.get(12);
    }

    public static int second(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return DateUtil.second(cal);
    }

    public static int second(Calendar calendar) {
        return calendar.get(13);
    }

    public static boolean equalsWithTolerance(TimeOfDay one, TimeOfDay two, Integer seconds) {
        if (null == seconds || 0 == seconds) {
            return one.equals(two);
        }
        return DateUtil.distance(one, two) <= (long)seconds.intValue();
    }

    public static long distance(TimeOfDay one, TimeOfDay two) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(11, one.getHour());
        calendar1.set(12, one.getMinute());
        calendar1.set(13, one.getSecond());
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(11, two.getHour());
        calendar2.set(12, two.getMinute());
        calendar2.set(13, two.getSecond());
        return Math.abs(calendar1.getTimeInMillis() / 1000L - calendar2.getTimeInMillis() / 1000L);
    }
}

