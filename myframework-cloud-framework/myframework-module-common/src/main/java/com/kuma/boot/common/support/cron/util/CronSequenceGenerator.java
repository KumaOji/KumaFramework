/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.cron.util;

import com.kuma.boot.common.support.cron.util.StringUtils;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

public class CronSequenceGenerator {
    private final String expression;
    private final TimeZone timeZone;
    private final BitSet months = new BitSet(12);
    private final BitSet daysOfMonth = new BitSet(31);
    private final BitSet daysOfWeek = new BitSet(7);
    private final BitSet hours = new BitSet(24);
    private final BitSet minutes = new BitSet(60);
    private final BitSet seconds = new BitSet(60);

    public CronSequenceGenerator(String expression) {
        this(expression, TimeZone.getDefault());
    }

    public CronSequenceGenerator(String expression, TimeZone timeZone) {
        this.expression = expression;
        this.timeZone = timeZone;
        this.parse(expression);
    }

    private CronSequenceGenerator(String expression, String[] fields) {
        this.expression = expression;
        this.timeZone = null;
        this.doParse(fields);
    }

    String getExpression() {
        return this.expression;
    }

    public Date next(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        ((Calendar)calendar).setTimeZone(this.timeZone);
        calendar.setTime(date);
        calendar.set(14, 0);
        long originalTimestamp = calendar.getTimeInMillis();
        this.doNext(calendar, calendar.get(1));
        if (calendar.getTimeInMillis() == originalTimestamp) {
            ((Calendar)calendar).add(13, 1);
            this.doNext(calendar, calendar.get(1));
        }
        return calendar.getTime();
    }

    private void doNext(Calendar calendar, int dot) {
        int updateMinute;
        int minute;
        List<Integer> emptyList;
        int updateSecond;
        ArrayList<Integer> resets = new ArrayList<Integer>();
        int second = calendar.get(13);
        if (second == (updateSecond = this.findNext(this.seconds, second, calendar, 13, 12, emptyList = Collections.emptyList()))) {
            resets.add(13);
        }
        if ((minute = calendar.get(12)) == (updateMinute = this.findNext(this.minutes, minute, calendar, 12, 11, resets))) {
            resets.add(12);
        } else {
            this.doNext(calendar, dot);
        }
        int hour = calendar.get(11);
        int updateHour = this.findNext(this.hours, hour, calendar, 11, 7, resets);
        if (hour == updateHour) {
            resets.add(11);
        } else {
            this.doNext(calendar, dot);
        }
        int dayOfWeek = calendar.get(7);
        int dayOfMonth = calendar.get(5);
        int updateDayOfMonth = this.findNextDay(calendar, this.daysOfMonth, dayOfMonth, this.daysOfWeek, dayOfWeek, resets);
        if (dayOfMonth == updateDayOfMonth) {
            resets.add(5);
        } else {
            this.doNext(calendar, dot);
        }
        int month = calendar.get(2);
        int updateMonth = this.findNext(this.months, month, calendar, 2, 1, resets);
        if (month != updateMonth) {
            if (calendar.get(1) - dot > 4) {
                throw new IllegalArgumentException("Invalid cron expression \"" + this.expression + "\" led to runaway search for next trigger");
            }
            this.doNext(calendar, dot);
        }
    }

    private int findNextDay(Calendar calendar, BitSet daysOfMonth, int dayOfMonth, BitSet daysOfWeek, int dayOfWeek, List<Integer> resets) {
        int count = 0;
        int max = 366;
        while (!(daysOfMonth.get(dayOfMonth) && daysOfWeek.get(dayOfWeek - 1) || count++ >= max)) {
            calendar.add(5, 1);
            dayOfMonth = calendar.get(5);
            dayOfWeek = calendar.get(7);
            this.reset(calendar, resets);
        }
        if (count >= max) {
            throw new IllegalArgumentException("Overflow in day for expression \"" + this.expression + "\"");
        }
        return dayOfMonth;
    }

    private int findNext(BitSet bits, int value, Calendar calendar, int field, int nextField, List<Integer> lowerOrders) {
        int nextValue = bits.nextSetBit(value);
        if (nextValue == -1) {
            calendar.add(nextField, 1);
            this.reset(calendar, Collections.singletonList(field));
            nextValue = bits.nextSetBit(0);
        }
        if (nextValue != value) {
            calendar.set(field, nextValue);
            this.reset(calendar, lowerOrders);
        }
        return nextValue;
    }

    private void reset(Calendar calendar, List<Integer> fields) {
        Iterator<Integer> iterator = fields.iterator();
        int field = 0;
        while (iterator.hasNext()) {
            calendar.set(field, (field = iterator.next().intValue()) == 5 ? 1 : 0);
        }
    }

    private void parse(String expression) throws IllegalArgumentException {
        String[] fields = StringUtils.tokenizeToStringArray(expression, " ");
        if (!CronSequenceGenerator.areValidCronFields(fields)) {
            throw new IllegalArgumentException(String.format("Cron expression must consist of 6 fields (found %d in \"%s\")", fields.length, expression));
        }
        this.doParse(fields);
    }

    private void doParse(String[] fields) {
        this.setNumberHits(this.seconds, fields[0], 0, 60);
        this.setNumberHits(this.minutes, fields[1], 0, 60);
        this.setNumberHits(this.hours, fields[2], 0, 24);
        this.setDaysOfMonth(this.daysOfMonth, fields[3]);
        this.setMonths(this.months, fields[4]);
        this.setDays(this.daysOfWeek, this.replaceOrdinals(fields[5], "SUN,MON,TUE,WED,THU,FRI,SAT"), 8);
        if (this.daysOfWeek.get(7)) {
            this.daysOfWeek.set(0);
            this.daysOfWeek.clear(7);
        }
    }

    private String replaceOrdinals(String value, String commaSeparatedList) {
        String[] list = StringUtils.commaDelimitedListToStringArray(commaSeparatedList);
        for (int i = 0; i < list.length; ++i) {
            String item = list[i].toUpperCase();
            value = StringUtils.replace(value.toUpperCase(), item, "" + i);
        }
        return value;
    }

    private void setDaysOfMonth(BitSet bits, String field) {
        int max = 31;
        this.setDays(bits, field, max + 1);
        bits.clear(0);
    }

    private void setDays(BitSet bits, String field, int max) {
        if (field.contains("?")) {
            field = "*";
        }
        this.setNumberHits(bits, field, 0, max);
    }

    private void setMonths(BitSet bits, String value) {
        int max = 12;
        value = this.replaceOrdinals(value, "FOO,JAN,FEB,MAR,APR,MAY,JUN,JUL,AUG,SEP,OCT,NOV,DEC");
        BitSet months = new BitSet(13);
        this.setNumberHits(months, value, 1, max + 1);
        for (int i = 1; i <= max; ++i) {
            if (!months.get(i)) continue;
            bits.set(i - 1);
        }
    }

    private void setNumberHits(BitSet bits, String value, int min, int max) {
        String[] fields;
        for (String field : fields = StringUtils.delimitedListToStringArray(value, ",")) {
            int delta;
            if (!field.contains("/")) {
                int[] range = this.getRange(field, min, max);
                bits.set(range[0], range[1] + 1);
                continue;
            }
            String[] split = StringUtils.delimitedListToStringArray(field, "/");
            if (split.length > 2) {
                throw new IllegalArgumentException("Incrementer has more than two fields: '" + field + "' in expression \"" + this.expression + "\"");
            }
            int[] range = this.getRange(split[0], min, max);
            if (!split[0].contains("-")) {
                range[1] = max - 1;
            }
            if ((delta = Integer.valueOf(split[1]).intValue()) <= 0) {
                throw new IllegalArgumentException("Incrementer delta must be 1 or higher: '" + field + "' in expression \"" + this.expression + "\"");
            }
            for (int i = range[0]; i <= range[1]; i += delta) {
                bits.set(i);
            }
        }
    }

    private int[] getRange(String field, int min, int max) {
        int[] result = new int[2];
        if (field.contains("*")) {
            result[0] = min;
            result[1] = max - 1;
            return result;
        }
        if (!field.contains("-")) {
            result[0] = result[1] = Integer.valueOf(field).intValue();
        } else {
            String[] split = StringUtils.delimitedListToStringArray(field, "-");
            if (split.length > 2) {
                throw new IllegalArgumentException("Range has more than two fields: '" + field + "' in expression \"" + this.expression + "\"");
            }
            result[0] = Integer.valueOf(split[0]);
            result[1] = Integer.valueOf(split[1]);
        }
        if (result[0] >= max || result[1] >= max) {
            throw new IllegalArgumentException("Range exceeds maximum (" + max + "): '" + field + "' in expression \"" + this.expression + "\"");
        }
        if (result[0] < min || result[1] < min) {
            throw new IllegalArgumentException("Range less than minimum (" + min + "): '" + field + "' in expression \"" + this.expression + "\"");
        }
        if (result[0] > result[1]) {
            throw new IllegalArgumentException("Invalid inverted range: '" + field + "' in expression \"" + this.expression + "\"");
        }
        return result;
    }

    public static boolean isValidExpression(String expression) {
        if (expression == null) {
            return false;
        }
        String[] fields = StringUtils.tokenizeToStringArray(expression, " ");
        if (!CronSequenceGenerator.areValidCronFields(fields)) {
            return false;
        }
        try {
            new CronSequenceGenerator(expression, fields);
            return true;
        }
        catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private static boolean areValidCronFields(String[] fields) {
        return fields != null && fields.length == 6;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CronSequenceGenerator)) {
            return false;
        }
        CronSequenceGenerator otherCron = (CronSequenceGenerator)other;
        return this.months.equals(otherCron.months) && this.daysOfMonth.equals(otherCron.daysOfMonth) && this.daysOfWeek.equals(otherCron.daysOfWeek) && this.hours.equals(otherCron.hours) && this.minutes.equals(otherCron.minutes) && this.seconds.equals(otherCron.seconds);
    }

    public int hashCode() {
        return 17 * this.months.hashCode() + 29 * this.daysOfMonth.hashCode() + 37 * this.daysOfWeek.hashCode() + 41 * this.hours.hashCode() + 53 * this.minutes.hashCode() + 61 * this.seconds.hashCode();
    }

    public String toString() {
        return this.getClass().getSimpleName() + ": " + this.expression;
    }
}

