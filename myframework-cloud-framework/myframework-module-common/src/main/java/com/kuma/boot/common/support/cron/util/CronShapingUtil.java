/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.cron.util;

import com.kuma.boot.common.support.cron.pojo.CronPosition;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CronShapingUtil {
    private static final Map<String, String> MONTH_MAP = new HashMap<String, String>(12);
    private static final Map<String, String> WEEK_MAP = new HashMap<String, String>(7);

    public static String shaping(String express, CronPosition cronPosition) {
        if (cronPosition == CronPosition.MONTH) {
            express = CronShapingUtil.shapingMonth(express);
        }
        if (cronPosition == CronPosition.WEEK) {
            express = CronShapingUtil.shapingWeek(express);
            express = express.replace("?", "*");
        }
        if (cronPosition == CronPosition.DAY) {
            express = express.replace("?", "*");
        }
        return express;
    }

    private static String shapingMonth(String express) {
        Set<Map.Entry<String, String>> entrySet = MONTH_MAP.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            if (!express.toUpperCase().contains(entry.getKey())) continue;
            express = express.toUpperCase().replace(entry.getKey(), entry.getValue());
        }
        return express;
    }

    private static String shapingWeek(String express) {
        Set<Map.Entry<String, String>> entrySet = WEEK_MAP.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            if (!express.toUpperCase().contains(entry.getKey())) continue;
            express = express.toUpperCase().replace(entry.getKey(), entry.getValue());
        }
        return express;
    }

    static {
        MONTH_MAP.put("JAN", "1");
        MONTH_MAP.put("FEB", "2");
        MONTH_MAP.put("MAR", "3");
        MONTH_MAP.put("APR", "4");
        MONTH_MAP.put("May", "5");
        MONTH_MAP.put("JUN", "6");
        MONTH_MAP.put("JUL", "7");
        MONTH_MAP.put("AUG", "8");
        MONTH_MAP.put("SEP", "9");
        MONTH_MAP.put("OCT", "10");
        MONTH_MAP.put("NOV", "11");
        MONTH_MAP.put("DEC", "12");
        WEEK_MAP.put("SUN", "0");
        WEEK_MAP.put("MON", "1");
        WEEK_MAP.put("TUE", "2");
        WEEK_MAP.put("WED", "3");
        WEEK_MAP.put("THU", "4");
        WEEK_MAP.put("FRI", "5");
        WEEK_MAP.put("SAT", "6");
    }
}

