/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.crypto.SecureUtil
 *  jakarta.servlet.http.HttpServletRequest
 */
package com.kuma.boot.common.utils.sql;

import cn.hutool.crypto.SecureUtil;
import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.regex.Pattern;

public class SqlInjectionUtil {
    private static final String TABLE_DICT_SIGN_SALT = "20200501";
    private static final String XSS_STR = "and |exec |insert |select |delete |update |drop |count |chr |mid |master |truncate |char |declare |;|or |+|user()";
    private static final String REGULAR_EXPRE_USER = "user[\\s]*\\([\\s]*\\)";
    private static final String SHOW_TABLES = "show\\s+tables";

    public static void checkDictTableSign(String dictCode, String sign, HttpServletRequest request) {
        String accessToken = request.getHeader("X-Access-Token");
        String signStr = dictCode + TABLE_DICT_SIGN_SALT + accessToken;
        String javaSign = SecureUtil.md5((String)signStr);
        if (!javaSign.equals(sign)) {
            LogUtils.error("\u8868\u5b57\u5178\uff0cSQL\u6ce8\u5165\u6f0f\u6d1e\u7b7e\u540d\u6821\u9a8c\u5931\u8d25 \uff1a" + sign + "!=" + javaSign + ",dictCode=" + dictCode, new Object[0]);
            throw new BaseException("\u65e0\u6743\u9650\u8bbf\u95ee\uff01");
        }
        LogUtils.info(" \u8868\u5b57\u5178\uff0cSQL\u6ce8\u5165\u6f0f\u6d1e\u7b7e\u540d\u6821\u9a8c\u6210\u529f\uff01sign=" + sign + ",dictCode=" + dictCode, new Object[0]);
    }

    public static void filterContent(String value) {
        SqlInjectionUtil.filterContent(value, null);
    }

    public static void filterContent(String value, String customXssString) {
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
        if (customXssString != null) {
            String[] xssArr2 = customXssString.split("\\|");
            for (int i = 0; i < xssArr2.length; ++i) {
                if (!value.contains(xssArr2[i])) continue;
                LogUtils.error("\u8bf7\u6ce8\u610f\uff0c\u5b58\u5728SQL\u6ce8\u5165\u5173\u952e\u8bcd---> {}", xssArr2[i]);
                LogUtils.error("\u8bf7\u6ce8\u610f\uff0c\u503c\u53ef\u80fd\u5b58\u5728SQL\u6ce8\u5165\u98ce\u9669!---> {}", value);
                throw new RuntimeException("\u8bf7\u6ce8\u610f\uff0c\u503c\u53ef\u80fd\u5b58\u5728SQL\u6ce8\u5165\u98ce\u9669!--->" + value);
            }
        }
        if (Pattern.matches(SHOW_TABLES, value) || Pattern.matches(REGULAR_EXPRE_USER, value)) {
            throw new RuntimeException("\u8bf7\u6ce8\u610f\uff0c\u503c\u53ef\u80fd\u5b58\u5728SQL\u6ce8\u5165\u98ce\u9669!--->" + value);
        }
    }

    public static void filterContent(String[] values) {
        SqlInjectionUtil.filterContent(values, null);
    }

    public static void filterContent(String[] values, String customXssString) {
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
            if (customXssString != null) {
                String[] xssArr2 = customXssString.split("\\|");
                for (int i = 0; i < xssArr2.length; ++i) {
                    if (value.indexOf(xssArr2[i]) <= -1) continue;
                    LogUtils.error("\u8bf7\u6ce8\u610f\uff0c\u5b58\u5728SQL\u6ce8\u5165\u5173\u952e\u8bcd---> {}", xssArr2[i]);
                    LogUtils.error("\u8bf7\u6ce8\u610f\uff0c\u503c\u53ef\u80fd\u5b58\u5728SQL\u6ce8\u5165\u98ce\u9669!---> {}", value);
                    throw new RuntimeException("\u8bf7\u6ce8\u610f\uff0c\u503c\u53ef\u80fd\u5b58\u5728SQL\u6ce8\u5165\u98ce\u9669!--->" + value);
                }
            }
            if (!Pattern.matches(SHOW_TABLES, value) && !Pattern.matches(REGULAR_EXPRE_USER, value)) continue;
            throw new RuntimeException("\u8bf7\u6ce8\u610f\uff0c\u503c\u53ef\u80fd\u5b58\u5728SQL\u6ce8\u5165\u98ce\u9669!--->" + value);
        }
    }

    @Deprecated
    public static void specialFilterContentForDictSql(String value) {
        String specialXssStr = " exec | insert | select | delete | update | drop | count | chr | mid | master | truncate | char | declare |;|+|user()";
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
        if (Pattern.matches(SHOW_TABLES, value) || Pattern.matches(REGULAR_EXPRE_USER, value)) {
            throw new RuntimeException("\u8bf7\u6ce8\u610f\uff0c\u503c\u53ef\u80fd\u5b58\u5728SQL\u6ce8\u5165\u98ce\u9669!--->" + value);
        }
    }

    @Deprecated
    public static void specialFilterContentForOnlineReport(String value) {
        String specialXssStr = " exec | insert | delete | update | drop | chr | mid | master | truncate | char | declare |user()";
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
        if (Pattern.matches(SHOW_TABLES, value) || Pattern.matches(REGULAR_EXPRE_USER, value)) {
            throw new RuntimeException("\u8bf7\u6ce8\u610f\uff0c\u503c\u53ef\u80fd\u5b58\u5728SQL\u6ce8\u5165\u98ce\u9669!--->" + value);
        }
    }

    public static boolean isClassField(String field, Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            String fieldName = fields[i].getName();
            String tableColumnName = StringUtils.camelToUnderline(fieldName);
            if (!fieldName.equalsIgnoreCase(field) && !tableColumnName.equalsIgnoreCase(field)) continue;
            return true;
        }
        return false;
    }

    public static boolean isClassField(Set<String> fieldSet, Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (String field : fieldSet) {
            boolean exist = false;
            for (int i = 0; i < fields.length; ++i) {
                String fieldName = fields[i].getName();
                String tableColumnName = StringUtils.camelToUnderline(fieldName);
                if (!fieldName.equalsIgnoreCase(field) && !tableColumnName.equalsIgnoreCase(field)) continue;
                exist = true;
                break;
            }
            if (exist) continue;
            return false;
        }
        return true;
    }
}

