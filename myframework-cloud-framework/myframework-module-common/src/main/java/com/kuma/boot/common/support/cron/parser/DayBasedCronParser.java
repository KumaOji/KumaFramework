/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.cron.parser;

import com.kuma.boot.common.support.cron.parser.CronParser;
import com.kuma.boot.common.support.cron.pojo.CronField;
import com.kuma.boot.common.support.cron.pojo.CronPosition;
import com.kuma.boot.common.support.cron.pojo.TimeOfDay;
import com.kuma.boot.common.support.cron.util.CompareUtil;
import com.kuma.boot.common.support.cron.util.CronUtil;
import com.kuma.boot.common.support.cron.util.DateUtil;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DayBasedCronParser
implements CronParser {
    private static final int CRON_LEN_YEAR = 7;
    private final String expression;
    private final TimeZone timeZone;
    private final List<CronField> cronFields;
    private final CronField fieldSecond;
    private final CronField fieldMinute;
    private final CronField fieldHour;
    private final CronField fieldDay;
    private final CronField fieldMonth;
    private final CronField fieldWeek;
    private final CronField fieldYear;

    public DayBasedCronParser(String expression) {
        this(expression, TimeZone.getDefault());
    }

    public DayBasedCronParser(String expression, TimeZone timeZone) {
        this.expression = expression;
        this.timeZone = timeZone;
        this.cronFields = CronUtil.convertCronField(expression);
        this.fieldSecond = this.cronFields.get(CronPosition.SECOND.ordinal());
        this.fieldMinute = this.cronFields.get(CronPosition.MINUTE.ordinal());
        this.fieldHour = this.cronFields.get(CronPosition.HOUR.ordinal());
        this.fieldDay = this.cronFields.get(CronPosition.DAY.ordinal());
        this.fieldMonth = this.cronFields.get(CronPosition.MONTH.ordinal());
        this.fieldWeek = this.cronFields.get(CronPosition.WEEK.ordinal());
        this.fieldYear = 7 == this.cronFields.size() ? this.cronFields.get(CronPosition.YEAR.ordinal()) : null;
    }

    @Override
    public Date next(Date date) {
        List<Integer> listYear;
        Integer calYear;
        Integer year;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(this.timeZone);
        calendar.setTime(date);
        calendar.add(13, 1);
        if (null != this.fieldYear && !(year = Integer.valueOf(DateUtil.year(calendar))).equals(calYear = CompareUtil.findNext(year, listYear = this.fieldYear.points()))) {
            calendar.set(1, calYear);
        }
        return CronUtil.doNext(calendar, this.fieldSecond, this.fieldMinute, this.fieldHour, this.fieldDay, this.fieldMonth, this.fieldWeek, this.fieldYear);
    }

    @Override
    public List<TimeOfDay> timesOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(this.timeZone);
        calendar.setTime(date);
        int year = DateUtil.year(calendar);
        int week = DateUtil.week(calendar);
        int month = DateUtil.month(calendar);
        int day = DateUtil.day(calendar);
        if (null != this.fieldYear && !CronUtil.satisfy(year, this.fieldYear)) {
            return Collections.emptyList();
        }
        if (!(CronUtil.satisfy(week, this.fieldWeek) && CronUtil.satisfy(month, this.fieldMonth) && CronUtil.satisfy(day, this.fieldDay))) {
            return Collections.emptyList();
        }
        CronField fieldHour = this.cronFields.get(CronPosition.HOUR.ordinal());
        CronField fieldMinute = this.cronFields.get(CronPosition.MINUTE.ordinal());
        CronField fieldSecond = this.cronFields.get(CronPosition.SECOND.ordinal());
        return CronUtil.timesOfDay(fieldHour, fieldMinute, fieldSecond);
    }
}

