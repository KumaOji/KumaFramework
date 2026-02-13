/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.model;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DatePattern {
    public static final String NORM_DATE_PATTERN = "yyyy-MM-dd";
    public static final String COLON_DATE_PATTERN = "yyyy:MM:dd";
    public static final DateTimeFormatter NORM_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());
    public static final String NORM_TIME_PATTERN = "HH:mm:ss";
    public static final DateTimeFormatter NORM_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());
    public static final String NORM_DATETIME_MINUTE_PATTERN = "yyyy-MM-dd HH:mm";
    public static final DateTimeFormatter NORM_DATETIME_MINUTE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());
    public static final String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter NORM_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
    public static final String NORM_DATETIME_MS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final DateTimeFormatter NORM_DATETIME_MS_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.systemDefault());
    public static final String CHINESE_DATE_PATTERN = "yyyy\u5e74MM\u6708dd\u65e5";
    public static final DateTimeFormatter CHINESE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy\u5e74MM\u6708dd\u65e5").withZone(ZoneId.systemDefault());
    public static final String PURE_DATE_PATTERN = "yyyyMMdd";
    public static final DateTimeFormatter PURE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.systemDefault());
    public static final String PURE_TIME_PATTERN = "HHmmss";
    public static final DateTimeFormatter PURE_TIME_FORMAT = DateTimeFormatter.ofPattern("HHmmss");
    public static final String PURE_DATETIME_PATTERN = "yyyyMMddHHmmss";
    public static final DateTimeFormatter PURE_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    public static final String PURE_DATETIME_MS_PATTERN = "yyyyMMddHHmmssSSS";
    public static final DateTimeFormatter PURE_DATETIME_MS_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    public static final String HTTP_DATETIME_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";
    public static final DateTimeFormatter HTTP_DATETIME_FORMAT = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.US).withZone(ZoneId.of("GMT"));
    public static final String JDK_DATETIME_PATTERN = "EEE MMM dd HH:mm:ss zzz yyyy";
    public static final DateTimeFormatter JDK_DATETIME_FORMAT = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
    public static final String UTC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final DateTimeFormatter UTC_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC);
    public static final String UTC_WITH_ZONE_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final DateTimeFormatter UTC_WITH_ZONE_OFFSET_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ").withZone(ZoneOffset.UTC);
    public static final String UTC_MS_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final DateTimeFormatter UTC_MS_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);
    public static final String UTC_MS_WITH_ZONE_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final DateTimeFormatter UTC_MS_WITH_ZONE_OFFSET_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").withZone(ZoneOffset.UTC);
}

