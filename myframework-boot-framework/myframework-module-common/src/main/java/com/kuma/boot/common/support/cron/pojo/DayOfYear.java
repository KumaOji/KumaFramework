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

package com.kuma.boot.common.support.cron.pojo;

import java.util.Calendar;

/** 保存日月年 */
public final class DayOfYear implements Comparable<DayOfYear> {

    private int day;

    private int month;

    private int year;

    public DayOfYear(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DayOfYear dayOfYear = (DayOfYear) o;

        if (day != dayOfYear.day) {
            return false;
        }
        if (month != dayOfYear.month) {
            return false;
        }
        return year == dayOfYear.year;
    }

    @Override
    public int hashCode() {
        int result = day;
        result = 31 * result + month;
        result = 31 * result + year;
        return result;
    }

    /** 计算星期 */
    public int week() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, getYear());
        calendar.set(Calendar.DAY_OF_MONTH, getDay());
        calendar.set(Calendar.MONTH, getMonth() - 1);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /** 按照年月日的顺序逐个比较 */
    @Override
    public int compareTo(DayOfYear o) {
        if (this.getYear() > o.getYear()) {
            return 1;
        }
        if (this.getYear() < o.getYear()) {
            return -1;
        }
        if (this.getMonth() > o.getMonth()) {
            return 1;
        }
        if (this.getMonth() < o.getMonth()) {
            return -1;
        }
        if (this.getDay() > o.getDay()) {
            return 1;
        }
        if (this.getDay() < o.getDay()) {
            return -1;
        }
        return 0;
    }
}
