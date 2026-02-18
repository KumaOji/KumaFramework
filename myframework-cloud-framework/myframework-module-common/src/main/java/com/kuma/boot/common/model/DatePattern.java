/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.model;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * 常用日期格式
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 19:41:13
 */
public class DatePattern  {

    // --------------------------------------------------------------------------------------------------------------------------------
    // Normal
    /** 标准日期格式：yyyy-MM-dd */
    public static final String NORM_DATE_PATTERN = "yyyy-MM-dd";

    public static final String COLON_DATE_PATTERN = "yyyy:MM:dd";

    /** 标准日期格式 {@link DateTimeFormatter}：yyyy-MM-dd */
    public static final DateTimeFormatter NORM_DATE_FORMAT =
            DateTimeFormatter.ofPattern(NORM_DATE_PATTERN).withZone(ZoneId.systemDefault());

    /** 标准时间格式：HH:mm:ss */
    public static final String NORM_TIME_PATTERN = "HH:mm:ss";

    /** 标准时间格式 {@link DateTimeFormatter}：HH:mm:ss */
    public static final DateTimeFormatter NORM_TIME_FORMAT =
            DateTimeFormatter.ofPattern(NORM_TIME_PATTERN).withZone(ZoneId.systemDefault());

    /** 标准日期时间格式，精确到分：yyyy-MM-dd HH:mm */
    public static final String NORM_DATETIME_MINUTE_PATTERN = "yyyy-MM-dd HH:mm";

    /** 标准日期时间格式，精确到分 {@link DateTimeFormatter}：yyyy-MM-dd HH:mm */
    public static final DateTimeFormatter NORM_DATETIME_MINUTE_FORMAT =
            DateTimeFormatter.ofPattern(NORM_DATETIME_MINUTE_PATTERN)
                    .withZone(ZoneId.systemDefault());

    /** 标准日期时间格式，精确到秒：yyyy-MM-dd HH:mm:ss */
    public static final String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /** 标准日期时间格式，精确到秒 {@link DateTimeFormatter}：yyyy-MM-dd HH:mm:ss */
    public static final DateTimeFormatter NORM_DATETIME_FORMAT =
            DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN).withZone(ZoneId.systemDefault());

    /** 标准日期时间格式，精确到毫秒：yyyy-MM-dd HH:mm:ss.SSS */
    public static final String NORM_DATETIME_MS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    /** 标准日期时间格式，精确到毫秒 {@link DateTimeFormatter}：yyyy-MM-dd HH:mm:ss.SSS */
    public static final DateTimeFormatter NORM_DATETIME_MS_FORMAT =
            DateTimeFormatter.ofPattern(NORM_DATETIME_MS_PATTERN).withZone(ZoneId.systemDefault());

    /** 标准日期格式：yyyy年MM月dd日 */
    public static final String CHINESE_DATE_PATTERN = "yyyy年MM月dd日";

    /** 标准日期格式 {@link DateTimeFormatter}：yyyy年MM月dd日 */
    public static final DateTimeFormatter CHINESE_DATE_FORMAT =
            DateTimeFormatter.ofPattern(CHINESE_DATE_PATTERN).withZone(ZoneId.systemDefault());

    // --------------------------------------------------------------------------------------------------------------------------------
    // Pure
    /** 标准日期格式：yyyyMMdd */
    public static final String PURE_DATE_PATTERN = "yyyyMMdd";

    /** 标准日期格式 {@link DateTimeFormatter}：yyyyMMdd */
    public static final DateTimeFormatter PURE_DATE_FORMAT =
            DateTimeFormatter.ofPattern(PURE_DATE_PATTERN).withZone(ZoneId.systemDefault());

    /** 标准日期格式：HHmmss */
    public static final String PURE_TIME_PATTERN = "HHmmss";

    /** 标准日期格式 {@link DateTimeFormatter}：HHmmss */
    public static final DateTimeFormatter PURE_TIME_FORMAT =
            DateTimeFormatter.ofPattern(PURE_TIME_PATTERN);

    /** 标准日期格式：yyyyMMddHHmmss */
    public static final String PURE_DATETIME_PATTERN = "yyyyMMddHHmmss";

    /** 标准日期格式 {@link DateTimeFormatter}：yyyyMMddHHmmss */
    public static final DateTimeFormatter PURE_DATETIME_FORMAT =
            DateTimeFormatter.ofPattern(PURE_DATETIME_PATTERN);

    /** 标准日期格式：yyyyMMddHHmmssSSS */
    public static final String PURE_DATETIME_MS_PATTERN = "yyyyMMddHHmmssSSS";

    /** 标准日期格式 {@link DateTimeFormatter}：yyyyMMddHHmmssSSS */
    public static final DateTimeFormatter PURE_DATETIME_MS_FORMAT =
            DateTimeFormatter.ofPattern(PURE_DATETIME_MS_PATTERN);

    // --------------------------------------------------------------------------------------------------------------------------------
    // Others
    /** HTTP头中日期时间格式：EEE, dd MMM yyyy HH:mm:ss z */
    public static final String HTTP_DATETIME_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";

    /** HTTP头中日期时间格式 {@link DateTimeFormatter}：EEE, dd MMM yyyy HH:mm:ss z */
    public static final DateTimeFormatter HTTP_DATETIME_FORMAT =
            DateTimeFormatter.ofPattern(HTTP_DATETIME_PATTERN, Locale.US)
                    .withZone(ZoneId.of("GMT"));

    /** JDK中日期时间格式：EEE MMM dd HH:mm:ss zzz yyyy */
    public static final String JDK_DATETIME_PATTERN = "EEE MMM dd HH:mm:ss zzz yyyy";

    /** JDK中日期时间格式 {@link DateTimeFormatter}：EEE MMM dd HH:mm:ss zzz yyyy */
    public static final DateTimeFormatter JDK_DATETIME_FORMAT =
            DateTimeFormatter.ofPattern(JDK_DATETIME_PATTERN, Locale.US);

    /** UTC时间：yyyy-MM-dd'T'HH:mm:ss'Z' */
    public static final String UTC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /** UTC时间{@link DateTimeFormatter}：yyyy-MM-dd'T'HH:mm:ss'Z' */
    public static final DateTimeFormatter UTC_FORMAT =
            DateTimeFormatter.ofPattern(UTC_PATTERN).withZone(ZoneOffset.UTC);

    /** UTC时间：yyyy-MM-dd'T'HH:mm:ssZ */
    public static final String UTC_WITH_ZONE_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";

    /** UTC时间{@link DateTimeFormatter}：yyyy-MM-dd'T'HH:mm:ssZ */
    public static final DateTimeFormatter UTC_WITH_ZONE_OFFSET_FORMAT =
            DateTimeFormatter.ofPattern(UTC_WITH_ZONE_OFFSET_PATTERN).withZone(ZoneOffset.UTC);

    /** UTC时间：yyyy-MM-dd'T'HH:mm:ss.SSS'Z' */
    public static final String UTC_MS_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /** UTC时间{@link DateTimeFormatter}：yyyy-MM-dd'T'HH:mm:ss.SSS'Z' */
    public static final DateTimeFormatter UTC_MS_FORMAT =
            DateTimeFormatter.ofPattern(UTC_MS_PATTERN).withZone(ZoneOffset.UTC);

    /** UTC时间：yyyy-MM-dd'T'HH:mm:ssZ */
    public static final String UTC_MS_WITH_ZONE_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    /** UTC时间{@link DateTimeFormatter}：yyyy-MM-dd'T'HH:mm:ssZ */
    public static final DateTimeFormatter UTC_MS_WITH_ZONE_OFFSET_FORMAT =
            DateTimeFormatter.ofPattern(UTC_MS_WITH_ZONE_OFFSET_PATTERN).withZone(ZoneOffset.UTC);
}
