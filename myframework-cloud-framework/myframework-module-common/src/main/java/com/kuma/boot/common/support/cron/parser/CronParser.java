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

package com.kuma.boot.common.support.cron.parser;

import com.kuma.boot.common.support.cron.pojo.TimeOfDay;
import java.util.Date;
import java.util.List;

/** CRON表达式解析 */
public interface CronParser {

    /**
     * 某个时刻的下一个时刻
     * @param date 给定时刻
     * @return 下一个执行时刻
     */
    Date next(Date date);

    /**
     * 计算一天中的哪些时刻[时分秒]执行
     * @param date 给定日期
     * @return 哪些时刻[时分秒]
     */
    List<TimeOfDay> timesOfDay(Date date);
}
