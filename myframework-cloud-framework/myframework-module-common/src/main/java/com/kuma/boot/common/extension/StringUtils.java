/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.common.extension;

import cn.hutool.core.util.StrUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class StringUtils {
    public static boolean isNotNull(String str) {
        return !StringUtils.isNull(str);
    }

    public static boolean isNull(String str) {
        return StrUtil.isBlank((CharSequence)str) || "null".equals(StrUtil.trimToEmpty((CharSequence)str));
    }

    public static String camelToUnderline(String str) {
        int len = str.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; ++i) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append('_');
                sb.append(Character.toLowerCase(c));
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String underlineToCamel(String str) {
        int len = str.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; ++i) {
            char c = str.charAt(i);
            if (c == '_') {
                if (++i >= len) continue;
                sb.append(Character.toUpperCase(str.charAt(i)));
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static List<String> toStringList(String str, String split) {
        return Arrays.asList(str.split(split));
    }

    public static String camelToSplitName(String camelName, String split) {
        if (StringUtils.isEmpty(camelName)) {
            return camelName;
        }
        StringBuilder buf = null;
        for (int i = 0; i < camelName.length(); ++i) {
            char ch = camelName.charAt(i);
            if (ch >= 'A' && ch <= 'Z') {
                if (buf == null) {
                    buf = new StringBuilder();
                    if (i > 0) {
                        buf.append(camelName, 0, i);
                    }
                }
                if (i > 0) {
                    buf.append(split);
                }
                buf.append(Character.toLowerCase(ch));
                continue;
            }
            if (buf == null) continue;
            buf.append(ch);
        }
        return buf == null ? camelName : buf.toString();
    }

    public static String nullToEmpty(Object str) {
        return str != null ? str.toString() : "";
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static String subString2(String str, int maxlen) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        if (str.length() <= maxlen) {
            return str;
        }
        return str.substring(0, maxlen);
    }

    public static String subString3(String str, int maxlen) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        if (str.length() <= maxlen) {
            return str;
        }
        return str.substring(0, maxlen) + "...";
    }

    public static String trimLeft(String str, char trim) {
        return org.springframework.util.StringUtils.trimLeadingCharacter((String)str, (char)trim);
    }

    public static String trimRight(String str, char trim) {
        return org.springframework.util.StringUtils.trimTrailingCharacter((String)str, (char)trim);
    }

    public static String trim(String str, char trim) {
        return StringUtils.trimRight(StringUtils.trimLeft(str, trim), trim);
    }

    public static String[] spilt(String str, String spiltChar) {
        if (StringUtils.isEmpty(str)) {
            return new String[0];
        }
        return str.split(spiltChar);
    }

    public static boolean hitCondition(String condition, String data) {
        if (StringUtils.isEmpty(condition) || data == null) {
            return false;
        }
        String[] skipUrls = condition.split(",");
        String trimChar = "*";
        for (String skip : skipUrls) {
            if ("*".equals(skip)) {
                return true;
            }
            String trimUrl = StringUtils.trim(skip, '*');
            if (!(!skip.startsWith(trimChar) && !skip.endsWith(trimChar) ? data.equals(trimUrl) : (skip.startsWith(trimChar) && skip.endsWith(trimChar) ? data.contains(trimUrl) : (skip.startsWith(trimChar) ? data.endsWith(trimUrl) : skip.endsWith(trimChar) && data.startsWith(trimUrl))))) continue;
            return true;
        }
        return false;
    }

    public static byte[] toArrays(InputStream input) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();){
            byte[] buffer = new byte[1024];
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
            output.flush();
            byte[] byArray = output.toByteArray();
            return byArray;
        }
    }

    public static byte[] toArrays(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    public static String toString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }
}

