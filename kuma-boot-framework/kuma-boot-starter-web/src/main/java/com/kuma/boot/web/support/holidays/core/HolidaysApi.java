/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.web.support.holidays.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 节假日接口
 *
 */
public interface HolidaysApi {

    /**
     * 获取日期类型
     *
     * @param localDate LocalDate
     * @return DaysType
     */
    com.kuma.boot.web.support.holidays.core.DaysType getDaysType(LocalDate localDate);

    /**
     * 获取日期类型
     *
     * @param localDateTime LocalDateTime
     * @return DaysType
     */
    default com.kuma.boot.web.support.holidays.core.DaysType getDaysType(LocalDateTime localDateTime) {
        return getDaysType(localDateTime.toLocalDate());
    }

    /**
     * 获取日期类型
     *
     * @param date Date
     * @return DaysType
     */
    default com.kuma.boot.web.support.holidays.core.DaysType getDaysType(Date date) {
        return getDaysType(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    /**
     * 判断是否工作日
     *
     * @param localDate LocalDate
     * @return 是否工作日
     */
    default boolean isWeekdays(LocalDate localDate) {
        return com.kuma.boot.web.support.holidays.core.DaysType.WEEKDAYS.equals(getDaysType(localDate));
    }

    /**
     * 判断是否工作日
     *
     * @param localDateTime LocalDateTime
     * @return 是否工作日
     */
    default boolean isWeekdays(LocalDateTime localDateTime) {
        return com.kuma.boot.web.support.holidays.core.DaysType.WEEKDAYS.equals(getDaysType(localDateTime));
    }

    /**
     * 判断是否工作日
     *
     * @param date Date
     * @return 是否工作日
     */
    default boolean isWeekdays(Date date) {
        return com.kuma.boot.web.support.holidays.core.DaysType.WEEKDAYS.equals(getDaysType(date));
    }
}
