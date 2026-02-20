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

package com.kuma.boot.common.utils.date;

import java.util.Calendar;

/**
 * 年龄工具类
 *
 * @author kuma
 * @version 2022.05
 * @since 2022-05-11 10:20:12
 */
public class AgeUtils {

    /**
     * 根据年月日计算年龄
     * @param birthDate 出生日期（格式：yyyy-MM-dd）
     * @return 年龄
     */
    public static int getAgeFromBirthDate(String birthDate) {
        // 先截取到字符串中的年、月、日
        String[] strs = birthDate.trim().split("-");
        int selectYear = Integer.parseInt(strs[0]);
        int selectMonth = Integer.parseInt(strs[1]);
        int selectDay = Integer.parseInt(strs[2]);
        // 得到当前时间的年、月、日
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayNow = cal.get(Calendar.DATE);

        // 用当前年月日减去生日年月日
        int yearMinus = yearNow - selectYear;
        int monthMinus = monthNow - selectMonth;
        int dayMinus = dayNow - selectDay;
        // 年龄初始值
        int age = 0;
        if (yearMinus > 0) {
            // 今年未过生日
            if (monthMinus <= 0 && dayMinus < 0) {
                age = yearMinus - 1;
                // 今年已过生日
            } else {
                age = yearMinus;
            }
        }
        return age;
    }

    /**
     * 根据年龄获得生日（默认月份日期-01-01）
     * @param age 年龄
     * @return 生日（日期格式）
     */
    public static String getBirthDateFromAge(int age) {
        // 得到当前时间的年、月、日
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        int birthdayYear = yearNow - age;
        return birthdayYear + "-01-01";
    }
}
