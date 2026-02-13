/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.common;

import com.kuma.boot.common.utils.lang.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CharsetUtils {
    private static final Pattern UNICODE_PATTERN = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");

    private CharsetUtils() {
    }

    public static String unicodeToZh(String unicode) {
        if (StringUtils.isEmpty(unicode)) {
            return null;
        }
        String[] strings = unicode.split("\\\\u");
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < strings.length; ++i) {
            String code = strings[i];
            String actualCode = code.substring(0, 4);
            char c = (char)Integer.valueOf(actualCode, 16).intValue();
            builder.append(c);
            if (code.length() <= 4) continue;
            builder.append(code.substring(4));
        }
        return builder.toString();
    }

    public static String zhToUnicode(String zh) {
        if (StringUtils.isEmpty(zh)) {
            return null;
        }
        char[] chars = zh.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (char aChar : chars) {
            builder.append("\\u").append(Integer.toString(aChar, 16));
        }
        return builder.toString();
    }

    public static boolean isChinese(char c) {
        boolean result = false;
        if (c >= '\u4e00' && c <= '\u29fa5') {
            result = true;
        }
        return result;
    }

    public static boolean isContainsChinese(String string) {
        char[] chars;
        if (StringUtils.isEmpty(string)) {
            return false;
        }
        for (char c : chars = string.toCharArray()) {
            if (!CharsetUtils.isChinese(c)) continue;
            return true;
        }
        return false;
    }

    public static boolean isAllChinese(String string) {
        char[] chars;
        if (StringUtils.isEmpty(string)) {
            return false;
        }
        for (char c : chars = string.toCharArray()) {
            if (CharsetUtils.isChinese(c)) continue;
            return false;
        }
        return true;
    }

    public static String unicodeToString(String unicodeText) {
        if (StringUtils.isEmptyTrim(unicodeText)) {
            return unicodeText;
        }
        Matcher matcher = UNICODE_PATTERN.matcher(unicodeText);
        while (matcher.find()) {
            char ch = (char)Integer.parseInt(matcher.group(2), 16);
            unicodeText = unicodeText.replace(matcher.group(1), "" + ch);
        }
        return unicodeText;
    }
}

