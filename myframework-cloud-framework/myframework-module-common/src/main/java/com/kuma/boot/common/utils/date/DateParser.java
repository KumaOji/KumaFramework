/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.date;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.regex.RegexUtils;
import java.util.AbstractMap;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DateParser {
    private static final Map<String, Map.Entry<Pattern, Map<Integer, Integer>>> patternAndIndexMapCache = new ConcurrentHashMap<String, Map.Entry<Pattern, Map<Integer, Integer>>>();
    private static final Map<String, Integer> fieldMap = new LinkedHashMap<String, Integer>();
    private final int length;
    private final String format;
    private final Pattern pattern;
    private Boolean amPm;
    private final Map<Integer, Integer> indexMap;

    public DateParser(String format) {
        if (StringUtils.isBlank(format)) {
            throw new IllegalArgumentException();
        }
        this.format = format;
        this.length = format.length();
        Map.Entry<Pattern, Map<Integer, Integer>> patternAndIndexMap = this.getPatternAndIndexMap(format);
        this.pattern = patternAndIndexMap.getKey();
        this.indexMap = patternAndIndexMap.getValue();
    }

    public DateParser(String format, boolean amPm) {
        this(format);
        this.amPm = amPm;
    }

    private Map.Entry<Pattern, Map<Integer, Integer>> getPatternAndIndexMap(String format) {
        Map.Entry patternAndIndexMap = patternAndIndexMapCache.get(format);
        if (patternAndIndexMap == null) {
            patternAndIndexMap = patternAndIndexMapCache.computeIfAbsent(format, this::generatePatternAndIndexMap);
        }
        return patternAndIndexMap;
    }

    private Map.Entry<Pattern, Map<Integer, Integer>> generatePatternAndIndexMap(String format) {
        format = RegexUtils.escape(format);
        int groupIndex = 1;
        LinkedHashMap<Integer, Integer> indexMap = new LinkedHashMap<Integer, Integer>();
        for (Map.Entry<String, Integer> entry : fieldMap.entrySet()) {
            String field = entry.getKey();
            if (!format.contains(field)) continue;
            format = format.replace(field, "(\\d{1," + field.length() + "})");
            indexMap.put(groupIndex++, entry.getValue());
        }
        return new AbstractMap.SimpleImmutableEntry<Pattern, Map<Integer, Integer>>(Pattern.compile(format), indexMap);
    }

    public Date parse(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() > this.length) {
            return null;
        }
        Matcher matcher = this.pattern.matcher(str);
        if (!matcher.matches()) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        if (this.amPm != null) {
            calendar.set(9, this.amPm != false ? 0 : 1);
        }
        for (Map.Entry<Integer, Integer> entry : this.indexMap.entrySet()) {
            String value = matcher.group(entry.getKey());
            int number = Integer.parseInt(value);
            if (entry.getValue() == 2) {
                --number;
            }
            calendar.set(entry.getValue(), number);
        }
        return calendar.getTime();
    }

    public String format(Date date) {
        return this.format(date, true);
    }

    String format(Date date, boolean zeroPadding) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        StringBuilder stringBuilder = new StringBuilder(this.format);
        for (Map.Entry<String, Integer> entry : fieldMap.entrySet()) {
            String format = entry.getKey();
            int value = entry.getValue();
            int index = 0;
            while ((index = stringBuilder.indexOf(format, index)) >= 0) {
                int number = calendar.get(value);
                if (value == 2) {
                    ++number;
                }
                String str = zeroPadding ? String.format("%0" + format.length() + "d", number) : Integer.toString(number);
                stringBuilder.replace(index, index + format.length(), str);
                index += format.length();
            }
        }
        return stringBuilder.toString();
    }

    public Boolean getAmPm() {
        return this.amPm;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DateParser that = (DateParser)o;
        return this.format.equals(that.format);
    }

    public int hashCode() {
        return Objects.hash(this.format);
    }

    static {
        fieldMap.put("yyyy", 1);
        fieldMap.put("MM", 2);
        fieldMap.put("dd", 5);
        fieldMap.put("HH", 11);
        fieldMap.put("a", 9);
        fieldMap.put("hh", 10);
        fieldMap.put("mm", 12);
        fieldMap.put("ss", 13);
        fieldMap.put("SSS", 14);
    }
}

