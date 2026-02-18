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

package com.kuma.boot.common.extension;

import cn.hutool.core.util.StrUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 字符串工具集
 *
 * @see org.apache.commons.lang3.StringUtils
 */
public class StringUtils {

    public static boolean isNotNull(String str) {
        return !isNull(str);
    }

    public static boolean isNull(String str) {
        return StrUtil.isBlank(str) || Strings.NULL.equals(StrUtil.trimToEmpty(str));
    }

    public static String camelToUnderline(String str) {
        int len = str.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(Chars.UNDERLINE);
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String underlineToCamel(String str) {
        int len = str.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (c == Chars.UNDERLINE) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(str.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static List<String> toStringList(String str, String split) {
        return Arrays.asList(str.split(split));
    }

    public static String camelToSplitName(String camelName, String split) {
        if (isEmpty(camelName)) {
            return camelName;
        }
        StringBuilder buf = null;
        for (int i = 0; i < camelName.length(); i++) {
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
            } else if (buf != null) {
                buf.append(ch);
            }
        }
        return buf == null ? camelName : buf.toString();
    }

    public static String nullToEmpty(Object str) {
        return str != null ? str.toString() : "";
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 部分字符串获取
     */
    public static String subString2(String str, int maxlen) {
        if (isEmpty(str)) {
            return str;
        }
        if (str.length() <= maxlen) {
            return str;
        }
        return str.substring(0, maxlen);
    }

    /**
     * 部分字符串获取 超出部分末尾...
     */
    public static String subString3(String str, int maxlen) {
        if (isEmpty(str)) {
            return str;
        }
        if (str.length() <= maxlen) {
            return str;
        }
        return str.substring(0, maxlen) + "...";
    }

    public static String trimLeft(String str, char trim) {
        return org.springframework.util.StringUtils.trimLeadingCharacter(str, trim);
    }

    public static String trimRight(String str, char trim) {
        return org.springframework.util.StringUtils.trimTrailingCharacter(str, trim);
    }

    public static String trim(String str, char trim) {
        return trimRight(trimLeft(str, trim), trim);
    }

    public static String[] spilt(String str, String spiltChar) {
        if (isEmpty(str)) {
            return new String[] {};
        }
        return str.split(spiltChar);
    }

    /**
     * ,分割,*做模糊匹配的 条件匹配算法
     */
    public static boolean hitCondition(String condition, String data) {
        if (StringUtils.isEmpty(condition) || data == null) {
            return false;
        }

        String[] skipUrls = condition.split(",");
        String trimChar = "*";
        for (String skip : skipUrls) {
            // 匹配所有
            if ("*".equals(skip)) {
                return true;
            }
            String trimUrl = StringUtils.trim(skip, '*');
            if (!skip.startsWith(trimChar) && !skip.endsWith(trimChar)) {
                if (data.equals(trimUrl)) {
                    return true;
                }
            } else if (skip.startsWith(trimChar) && skip.endsWith(trimChar)) {
                if (data.contains(trimUrl)) {
                    return true;
                }
            } else if (skip.startsWith(trimChar)) {
                if (data.endsWith(trimUrl)) {
                    return true;
                }
            } else if (skip.endsWith(trimChar)) {
                if (data.startsWith(trimUrl)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static byte[] toArrays(InputStream input) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
            output.flush();
            return output.toByteArray();
        }
    }

    public static byte[] toArrays(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    public static String toString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
