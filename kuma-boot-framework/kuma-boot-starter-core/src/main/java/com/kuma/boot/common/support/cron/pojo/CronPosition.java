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

/**
 * cron表达式某个位置上的一些常量，跟cron表达式的域一一对应 { 顺序 0 1 2 3 4 5 6 cron 0 15 10 ? * MON-FRI (2018)
 * cron域 SECOND、MINUTE、HOUR、DAY、MONTH、WEEK (、YEAR) }
 */
public enum CronPosition {
    SECOND(0, 59),
    MINUTE(0, 59),
    HOUR(0, 23),
    DAY(1, 31),
    MONTH(1, 12),
    WEEK(0, 6),
    YEAR(2018, 2099);

    /** 该域最小值 */
    private int min;

    /** 该域最大值 */
    private int max;

    CronPosition(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public static CronPosition fromPosition(int position) {
        for (CronPosition cronPosition : CronPosition.values()) {
            if (position == cronPosition.ordinal()) {
                return cronPosition;
            }
        }
        return null;
    }
}
