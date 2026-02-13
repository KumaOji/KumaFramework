/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.date;

import java.util.Calendar;

public class AgeUtils {
    public static int getAgeFromBirthDate(String birthDate) {
        String[] strs = birthDate.trim().split("-");
        int selectYear = Integer.parseInt(strs[0]);
        int selectMonth = Integer.parseInt(strs[1]);
        int selectDay = Integer.parseInt(strs[2]);
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(1);
        int monthNow = cal.get(2) + 1;
        int dayNow = cal.get(5);
        int yearMinus = yearNow - selectYear;
        int monthMinus = monthNow - selectMonth;
        int dayMinus = dayNow - selectDay;
        int age = 0;
        if (yearMinus > 0) {
            age = monthMinus <= 0 && dayMinus < 0 ? yearMinus - 1 : yearMinus;
        }
        return age;
    }

    public static String getBirthDateFromAge(int age) {
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(1);
        int birthdayYear = yearNow - age;
        return birthdayYear + "-01-01";
    }
}

