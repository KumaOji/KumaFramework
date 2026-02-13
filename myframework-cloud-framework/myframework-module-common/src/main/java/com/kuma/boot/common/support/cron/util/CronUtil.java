/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.cron.util;

import com.kuma.boot.common.support.cron.pojo.CronField;
import com.kuma.boot.common.support.cron.pojo.CronPosition;
import com.kuma.boot.common.support.cron.pojo.DayOfYear;
import com.kuma.boot.common.support.cron.pojo.TimeOfDay;
import com.kuma.boot.common.support.cron.util.CompareUtil;
import com.kuma.boot.common.support.cron.util.CronShapingUtil;
import com.kuma.boot.common.support.cron.util.DateUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CronUtil {
    private static final int CRON_LEN = 6;
    private static final int CRON_LEN_YEAR = 7;
    private static final String CRON_CUT = "\\s+";
    private static final int MAX_ADD_YEAR = 10;

    public static Date next(String cron, Date date) {
        List<Integer> listYear;
        Integer calYear;
        Integer year;
        List<CronField> cronFields = CronUtil.convertCronField(cron);
        CronField fieldSecond = cronFields.get(CronPosition.SECOND.ordinal());
        CronField fieldMinute = cronFields.get(CronPosition.MINUTE.ordinal());
        CronField fieldHour = cronFields.get(CronPosition.HOUR.ordinal());
        CronField fieldDay = cronFields.get(CronPosition.DAY.ordinal());
        CronField fieldMonth = cronFields.get(CronPosition.MONTH.ordinal());
        CronField fieldWeek = cronFields.get(CronPosition.WEEK.ordinal());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(13, 1);
        CronField fieldYear = null;
        if (7 == cronFields.size() && !(year = Integer.valueOf(DateUtil.year(calendar))).equals(calYear = CompareUtil.findNext(year, listYear = (fieldYear = cronFields.get(CronPosition.YEAR.ordinal())).points()))) {
            calendar.set(1, calYear);
        }
        return CronUtil.doNext(calendar, fieldSecond, fieldMinute, fieldHour, fieldDay, fieldMonth, fieldWeek, fieldYear);
    }

    public static Date doNext(Calendar calendar, CronField fieldSecond, CronField fieldMinute, CronField fieldHour, CronField fieldDay, CronField fieldMonth, CronField fieldWeek, CronField fieldYear) {
        TimeOfDay timeOfDayMin = CronUtil.doTimeOfDay(calendar, fieldSecond, fieldMinute, fieldHour);
        return CronUtil.doDayOfYear(0, calendar, fieldDay, fieldMonth, fieldWeek, fieldYear, timeOfDayMin);
    }

    private static Date doDayOfYear(int addYear, Calendar calendar, CronField fieldDay, CronField fieldMonth, CronField fieldWeek, CronField fieldYear, TimeOfDay timeOfDayMin) {
        List<Integer> listMonth;
        List<Integer> listDay;
        int monthNow;
        if (addYear >= 10) {
            throw new IllegalArgumentException("Invalid cron expression\u3010\u65e5\u6708\u5468\u5e74\u3011 which led to runaway search for next trigger");
        }
        int year = DateUtil.year(calendar);
        if (null != fieldYear && !CronUtil.satisfy(year, fieldYear)) {
            CronUtil.addOneYear(calendar, timeOfDayMin);
            return CronUtil.doDayOfYear(++addYear, calendar, fieldDay, fieldMonth, fieldWeek, fieldYear, timeOfDayMin);
        }
        int dayNow = DateUtil.day(calendar);
        DayOfYear dayOfYearNow = new DayOfYear(dayNow, monthNow = DateUtil.month(calendar), year);
        DayOfYear dayOfYearMin = CronUtil.findMinDayOfYear(dayOfYearNow, listDay = fieldDay.points(), listMonth = fieldMonth.points(), fieldWeek);
        if (null == dayOfYearMin) {
            CronUtil.addOneYear(calendar, timeOfDayMin);
            return CronUtil.doDayOfYear(++addYear, calendar, fieldDay, fieldMonth, fieldWeek, fieldYear, timeOfDayMin);
        }
        CronUtil.setDayOfYear(calendar, dayOfYearMin);
        if (dayOfYearNow.compareTo(dayOfYearMin) < 0) {
            CronUtil.setTimeOfDay(calendar, timeOfDayMin);
        }
        return calendar.getTime();
    }

    private static DayOfYear findMinDayOfYear(DayOfYear dayOfYearNow, List<Integer> listDay, List<Integer> listMonth, CronField fieldWeek) {
        for (Integer month : listMonth) {
            for (Integer day : listDay) {
                DayOfYear dayOfYear = new DayOfYear(day, month, dayOfYearNow.getYear());
                if (dayOfYear.compareTo(dayOfYearNow) < 0 || !CronUtil.satisfy(dayOfYear.week(), fieldWeek)) continue;
                return dayOfYear;
            }
        }
        return null;
    }

    private static void addOneYear(Calendar calendar, TimeOfDay timeOfDayMin) {
        CronUtil.setDayOfYear(calendar, 1, 1);
        CronUtil.setTimeOfDay(calendar, timeOfDayMin);
        calendar.add(1, 1);
    }

    private static void setDayOfYear(Calendar calendar, DayOfYear dayOfYear) {
        CronUtil.setDayOfYear(calendar, dayOfYear.getMonth(), dayOfYear.getDay());
    }

    private static void setDayOfYear(Calendar calendar, int month, int day) {
        calendar.set(2, month - 1);
        calendar.set(5, day);
    }

    private static TimeOfDay doTimeOfDay(Calendar calendar, CronField fieldSecond, CronField fieldMinute, CronField fieldHour) {
        int hourNow = DateUtil.hour(calendar);
        int minuteNow = DateUtil.minute(calendar);
        int secondNow = DateUtil.second(calendar);
        List<TimeOfDay> points = CronUtil.timesOfDay(fieldHour, fieldMinute, fieldSecond);
        TimeOfDay timeOfDayNow = new TimeOfDay(hourNow, minuteNow, secondNow);
        TimeOfDay timeOfDayMin = points.get(0);
        TimeOfDay timeOfDayMax = points.get(points.size() - 1);
        if (timeOfDayNow.compareTo(timeOfDayMin) < 0) {
            CronUtil.setTimeOfDay(calendar, timeOfDayMin);
        } else if (timeOfDayNow.compareTo(timeOfDayMax) > 0) {
            CronUtil.setTimeOfDay(calendar, timeOfDayMin);
            calendar.add(5, 1);
        } else {
            TimeOfDay next = CompareUtil.findNext(timeOfDayNow, points);
            CronUtil.setTimeOfDay(calendar, next);
        }
        return timeOfDayMin;
    }

    private static void setTimeOfDay(Calendar calendar, TimeOfDay timeOfDay) {
        calendar.set(11, timeOfDay.getHour());
        calendar.set(12, timeOfDay.getMinute());
        calendar.set(13, timeOfDay.getSecond());
    }

    public static List<TimeOfDay> timesOfDay(String cron, Date date) {
        CronField fieldYear;
        List<CronField> cronFields = CronUtil.convertCronField(cron);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = DateUtil.year(calendar);
        int week = DateUtil.week(calendar);
        int month = DateUtil.month(calendar);
        int day = DateUtil.day(calendar);
        if (7 == cronFields.size() && !CronUtil.satisfy(year, fieldYear = cronFields.get(CronPosition.YEAR.ordinal()))) {
            return Collections.emptyList();
        }
        CronField fieldWeek = cronFields.get(CronPosition.WEEK.ordinal());
        CronField fieldMonth = cronFields.get(CronPosition.MONTH.ordinal());
        CronField fieldDay = cronFields.get(CronPosition.DAY.ordinal());
        if (!(CronUtil.satisfy(week, fieldWeek) && CronUtil.satisfy(month, fieldMonth) && CronUtil.satisfy(day, fieldDay))) {
            return Collections.emptyList();
        }
        CronField fieldHour = cronFields.get(CronPosition.HOUR.ordinal());
        CronField fieldMinute = cronFields.get(CronPosition.MINUTE.ordinal());
        CronField fieldSecond = cronFields.get(CronPosition.SECOND.ordinal());
        return CronUtil.timesOfDay(fieldHour, fieldMinute, fieldSecond);
    }

    public static List<TimeOfDay> timesOfDay(CronField fieldHour, CronField fieldMinute, CronField fieldSecond) {
        List<Integer> listHour = fieldHour.points();
        List<Integer> listMinute = fieldMinute.points();
        List<Integer> listSecond = fieldSecond.points();
        ArrayList<TimeOfDay> points = new ArrayList<TimeOfDay>(listHour.size() * listMinute.size() * listSecond.size());
        for (Integer hour : listHour) {
            for (Integer minute : listMinute) {
                for (Integer second : listSecond) {
                    points.add(new TimeOfDay(hour, minute, second));
                }
            }
        }
        return points;
    }

    public static boolean satisfy(int fieldValue, CronField field) {
        return field.containsAll() || CompareUtil.inList(fieldValue, field.points());
    }

    public static List<CronField> convertCronField(String cron) {
        List<String> cut = CronUtil.cut(cron);
        int size = cut.size();
        if (6 != size && 7 != size) {
            throw new IllegalArgumentException("cron\u8868\u8fbe\u5f0f\u5fc5\u987b\u6709\u516d\u4e2a\u57df\u6216\u8005\u4e03\u4e2a\u57df(\u6700\u540e\u4e3a\u5e74)");
        }
        ArrayList<CronField> cronFields = new ArrayList<CronField>(size);
        for (int i = 0; i < size; ++i) {
            CronPosition cronPosition = CronPosition.fromPosition(i);
            cronFields.add(new CronField(cronPosition, CronShapingUtil.shaping(cut.get(i), cronPosition)));
        }
        return cronFields;
    }

    public static List<String> cut(String cron) {
        cron = cron.trim();
        String[] split = cron.split(CRON_CUT);
        return Arrays.asList(split);
    }
}

