/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.crypto.SecureUtil
 *  com.alibaba.fastjson2.JSON
 *  com.alibaba.fastjson2.JSONArray
 *  com.alibaba.fastjson2.JSONObject
 *  jakarta.servlet.http.HttpServletRequest
 */
package com.kuma.boot.common.utils.sql;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SqlInjectionUtils {
    private static final String SQL_REGEX = "\\b(and|or)\\b.{1,6}?(=|>|<|\\bin\\b|\\blike\\b)|\\/\\*.+?\\*\\/|<\\s*script\\b|\\bEXEC\\b|UNION.+?SELECT|UPDATE.+?SET|INSERT\\s+INTO.+?VALUES|(SELECT|DELETE).+?FROM|(CREATE|ALTER|DROP|TRUNCATE)\\s+(TABLE|DATABASE)";
    private static final Pattern SQL_PATTERN = Pattern.compile("\\b(and|or)\\b.{1,6}?(=|>|<|\\bin\\b|\\blike\\b)|\\/\\*.+?\\*\\/|<\\s*script\\b|\\bEXEC\\b|UNION.+?SELECT|UPDATE.+?SET|INSERT\\s+INTO.+?VALUES|(SELECT|DELETE).+?FROM|(CREATE|ALTER|DROP|TRUNCATE)\\s+(TABLE|DATABASE)", 2);
    private static final String TABLE_DICT_SIGN_SALT = "20200501";
    private static final String XSS_STR = "and |exec |insert |select |delete |update |drop |count |chr |mid |master |truncate |char |declare |;|or |+|user()";
    private static final String SHOW_TABLES = "show\\s+tables";

    private static boolean matching(String lowerValue, String param) {
        if (SQL_PATTERN.matcher(param).find()) {
            LogUtils.error("The parameter contains keywords {} that do not allow SQL!", lowerValue);
            return true;
        }
        return false;
    }

    private static String toLowerCase(Object obj) {
        return Optional.ofNullable(obj).map(Object::toString).map(String::toLowerCase).orElse("");
    }

    private static boolean checking(Object value) {
        String lowerValue = SqlInjectionUtils.toLowerCase(value);
        return SqlInjectionUtils.matching(lowerValue, lowerValue);
    }

    public static boolean checkForGet(String value) {
        String lowerValue = URLDecoder.decode(value, StandardCharsets.UTF_8).toLowerCase();
        return ((Stream)Stream.of(lowerValue.split("\\&")).map(kp -> kp.substring(kp.indexOf("=") + 1)).parallel()).anyMatch(param -> SqlInjectionUtils.matching(lowerValue, param));
    }

    public static boolean checkForPost(String value) {
        Object jsonObj = JSON.parse((String)value);
        if (jsonObj instanceof JSONObject) {
            JSONObject json = (JSONObject)jsonObj;
            return ((Stream)json.entrySet().stream().parallel()).anyMatch(entry -> SqlInjectionUtils.checking(entry.getValue()));
        }
        if (jsonObj instanceof JSONArray) {
            JSONArray json = (JSONArray)jsonObj;
            return ((Stream)json.stream().parallel()).anyMatch(SqlInjectionUtils::checking);
        }
        return false;
    }

    public static void checkDictTableSign(String dictCode, String sign, HttpServletRequest request) {
        String accessToken = request.getHeader("X-Access-Token");
        String signStr = dictCode + TABLE_DICT_SIGN_SALT + accessToken;
        String javaSign = SecureUtil.md5((String)signStr);
        if (!javaSign.equals(sign)) {
            LogUtils.error("\u8868\u5b57\u5178\uff0cSQL\u6ce8\u5165\u6f0f\u6d1e\u7b7e\u540d\u6821\u9a8c\u5931\u8d25 \uff1a" + sign + "!=" + javaSign + ",dictCode=" + dictCode, new Object[0]);
            throw new RuntimeException("\u65e0\u6743\u9650\u8bbf\u95ee\uff01");
        }
        LogUtils.info(" \u8868\u5b57\u5178\uff0cSQL\u6ce8\u5165\u6f0f\u6d1e\u7b7e\u540d\u6821\u9a8c\u6210\u529f\uff01sign=" + sign + ",dictCode=" + dictCode, new Object[0]);
    }

    public static void filterContent(String value) {
        if (value == null || "".equals(value)) {
            return;
        }
        value = value.toLowerCase();
        value = value.replaceAll("/\\*.*\\*/", "");
        String[] xssArr = XSS_STR.split("\\|");
        for (int i = 0; i < xssArr.length; ++i) {
            if (value.indexOf(xssArr[i]) <= -1) continue;
            LogUtils.error("\u8bf7\u6ce8\u610f\uff0c\u5b58\u5728SQL\u6ce8\u5165\u5173\u952e\u8bcd---> {}", xssArr[i]);
            LogUtils.error("\u8bf7\u6ce8\u610f\uff0c\u503c\u53ef\u80fd\u5b58\u5728SQL\u6ce8\u5165\u98ce\u9669!---> {}", value);
            throw new RuntimeException("\u8bf7\u6ce8\u610f\uff0c\u503c\u53ef\u80fd\u5b58\u5728SQL\u6ce8\u5165\u98ce\u9669!--->" + value);
        }
        if (Pattern.matches(SHOW_TABLES, value)) {
            throw new RuntimeException("\u8bf7\u6ce8\u610f\uff0c\u503c\u53ef\u80fd\u5b58\u5728SQL\u6ce8\u5165\u98ce\u9669!--->" + value);
        }
    }

    public static void filterContent(String[] values) {
        String[] xssArr = XSS_STR.split("\\|");
        for (String value : values) {
            if (value == null || "".equals(value)) {
                return;
            }
            value = value.toLowerCase();
            value = value.replaceAll("/\\*.*\\*/", "");
            for (int i = 0; i < xssArr.length; ++i) {
                if (value.indexOf(xssArr[i]) <= -1) continue;
                LogUtils.error("\u8bf7\u6ce8\u610f\uff0c\u5b58\u5728SQL\u6ce8\u5165\u5173\u952e\u8bcd---> {}", xssArr[i]);
                LogUtils.error("\u8bf7\u6ce8\u610f\uff0c\u503c\u53ef\u80fd\u5b58\u5728SQL\u6ce8\u5165\u98ce\u9669!---> {}", value);
                throw new RuntimeException("\u8bf7\u6ce8\u610f\uff0c\u503c\u53ef\u80fd\u5b58\u5728SQL\u6ce8\u5165\u98ce\u9669!--->" + value);
            }
            if (!Pattern.matches(SHOW_TABLES, value)) continue;
            throw new RuntimeException("\u8bf7\u6ce8\u610f\uff0c\u503c\u53ef\u80fd\u5b58\u5728SQL\u6ce8\u5165\u98ce\u9669!--->" + value);
        }
    }

    public static void specialFilterContent(String value) {
        String specialXssStr = " exec | insert | select | delete | update | drop | count | chr | mid | master | truncate | char | declare |;|+|";
        String[] xssArr = specialXssStr.split("\\|");
        if (value == null || "".equals(value)) {
            return;
        }
        value = value.toLowerCase();
        value = value.replaceAll("/\\*.*\\*/", "");
        for (int i = 0; i < xssArr.length; ++i) {
            if (value.indexOf(xssArr[i]) <= -1 && !value.startsWith(xssArr[i].trim())) continue;
            LogUtils.error("\u8bf7\u6ce8\u610f\uff0c\u5b58\u5728SQL\u6ce8\u5165\u5173\u952e\u8bcd---> {}", xssArr[i]);
            LogUtils.error("\u8bf7\u6ce8\u610f\uff0c\u503c\u53ef\u80fd\u5b58\u5728SQL\u6ce8\u5165\u98ce\u9669!---> {}", value);
            throw new RuntimeException("\u8bf7\u6ce8\u610f\uff0c\u503c\u53ef\u80fd\u5b58\u5728SQL\u6ce8\u5165\u98ce\u9669!--->" + value);
        }
        if (Pattern.matches(SHOW_TABLES, value)) {
            throw new RuntimeException("\u8bf7\u6ce8\u610f\uff0c\u503c\u53ef\u80fd\u5b58\u5728SQL\u6ce8\u5165\u98ce\u9669!--->" + value);
        }
    }

    public static void specialFilterContentForOnlineReport(String value) {
        String specialXssStr = " exec | insert | delete | update | drop | chr | mid | master | truncate | char | declare |";
        String[] xssArr = specialXssStr.split("\\|");
        if (value == null || "".equals(value)) {
            return;
        }
        value = value.toLowerCase();
        value = value.replaceAll("/\\*.*\\*/", "");
        for (int i = 0; i < xssArr.length; ++i) {
            if (value.indexOf(xssArr[i]) <= -1 && !value.startsWith(xssArr[i].trim())) continue;
            LogUtils.error("\u8bf7\u6ce8\u610f\uff0c\u5b58\u5728SQL\u6ce8\u5165\u5173\u952e\u8bcd---> {}", xssArr[i]);
            LogUtils.error("\u8bf7\u6ce8\u610f\uff0c\u503c\u53ef\u80fd\u5b58\u5728SQL\u6ce8\u5165\u98ce\u9669!---> {}", value);
            throw new RuntimeException("\u8bf7\u6ce8\u610f\uff0c\u503c\u53ef\u80fd\u5b58\u5728SQL\u6ce8\u5165\u98ce\u9669!--->" + value);
        }
        if (Pattern.matches(SHOW_TABLES, value)) {
            throw new RuntimeException("\u8bf7\u6ce8\u610f\uff0c\u503c\u53ef\u80fd\u5b58\u5728SQL\u6ce8\u5165\u98ce\u9669!--->" + value);
        }
    }
}

