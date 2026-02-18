/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.utils.date;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/** java8 时间工具类 */
public class LocalDateTimeUtil {

    /** 是否在指定的时间范围内 */
    public static boolean between(LocalDateTime now, LocalDateTime start, LocalDateTime end) {
        return ge(now, start) && le(now, end);
    }

    /** 大于 */
    public static boolean gt(LocalDateTime now, LocalDateTime next) {
        long mills = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long epochMilli = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return mills > epochMilli;
    }

    /** 小于 */
    public static boolean lt(LocalDateTime now, LocalDateTime next) {
        long mills = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long epochMilli = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return mills < epochMilli;
    }

    /** 大于等于 */
    public static boolean ge(LocalDateTime now, LocalDateTime next) {
        long mills = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long epochMilli = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return mills >= epochMilli;
    }

    /** 小于等于 */
    public static boolean le(LocalDateTime now, LocalDateTime next) {
        long mills = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long epochMilli = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return mills <= epochMilli;
    }

    /** 将localDate转换成localDateTime */
    public static LocalDateTime date2DateTime(LocalDate localDate) {
        return localDate.atTime(0, 0);
    }

    /**
     * 将long类型的timestamp转为LocalDateTime
     * @param timestamp 时间戳
     * @return LocalDateTime
     */
    public static LocalDateTime parse(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * LocalDateTime转为long类型的timestamp
     * @param localDateTime 日期时间
     * @return timestamp
     */
    public static long timestamp(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.toEpochMilli();
    }
}
