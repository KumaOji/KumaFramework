/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package com.kuma.boot.common.utils.common;

import com.kuma.boot.common.utils.lang.StringUtils;
import java.util.Arrays;
import org.jspecify.annotations.Nullable;

public final class DesensitizationUtils {
    public static @Nullable String chineseName(@Nullable String fullName) {
        return DesensitizationUtils.sensitive(fullName, 1, 0);
    }

    public static @Nullable String idCardNum(@Nullable String id) {
        return DesensitizationUtils.sensitive(id, 0, 4);
    }

    public static @Nullable String phoneNo(@Nullable String num) {
        return DesensitizationUtils.sensitive(num, 0, 4);
    }

    public static @Nullable String mobileNo(@Nullable String num) {
        return DesensitizationUtils.sensitive(num, 3, 4);
    }

    public static @Nullable String address(@Nullable String address, int sensitiveSize) {
        return DesensitizationUtils.sensitive(address, 0, sensitiveSize);
    }

    public static @Nullable String email(@Nullable String email) {
        if (StringUtils.isBlank(email)) {
            return "";
        }
        int index = email.indexOf(64);
        if (index <= 1) {
            return email;
        }
        return DesensitizationUtils.sensitive(email, 1, email.length() - index);
    }

    public static @Nullable String bankCard(@Nullable String cardNum) {
        return DesensitizationUtils.sensitive(cardNum, 6, 4);
    }

    public static @Nullable String cnapsCode(@Nullable String code) {
        return DesensitizationUtils.sensitive(code, 2, 0);
    }

    public static @Nullable String right(@Nullable String sensitiveStr) {
        if (StringUtils.isBlank(sensitiveStr)) {
            return "";
        }
        int length = sensitiveStr.length();
        return DesensitizationUtils.sensitive(sensitiveStr, length / 2, 0);
    }

    public static @Nullable String left(@Nullable String sensitiveStr) {
        if (StringUtils.isBlank(sensitiveStr)) {
            return "";
        }
        int length = sensitiveStr.length();
        return DesensitizationUtils.sensitive(sensitiveStr, 0, length / 2);
    }

    public static @Nullable String middle(@Nullable String sensitiveStr) {
        if (StringUtils.isBlank(sensitiveStr)) {
            return "";
        }
        int length = sensitiveStr.length();
        if (length < 3) {
            return StringUtils.leftPad("", length, '*');
        }
        char[] chars = new char[length];
        int last = length - 1;
        Arrays.fill(chars, 1, last, '*');
        chars[0] = sensitiveStr.charAt(0);
        chars[last] = sensitiveStr.charAt(last);
        return new String(chars);
    }

    public static @Nullable String all(@Nullable String sensitiveStr) {
        return DesensitizationUtils.sensitive(sensitiveStr, 0, 0);
    }

    public static @Nullable String sensitive(@Nullable String str, int fromIndex, int lastSize) {
        return DesensitizationUtils.sensitive(str, fromIndex, lastSize, '*');
    }

    public static @Nullable String sensitive(@Nullable String str, int fromIndex, int lastSize, int padSize) {
        return DesensitizationUtils.sensitive(str, fromIndex, lastSize, '*', padSize);
    }

    public static @Nullable String sensitive(@Nullable String str, int fromIndex, int lastSize, char padChar) {
        return DesensitizationUtils.sensitive(str, fromIndex, lastSize, padChar, -1);
    }

    public static @Nullable String sensitive(@Nullable String str, int fromIndex, int lastSize, char padChar, int padSize) {
        int padSiz;
        if (str == null) {
            return null;
        }
        if (StringUtils.isBlank(str)) {
            return "";
        }
        int length = str.length();
        if (fromIndex == 0 && lastSize == 0) {
            int padSiz2 = padSize > 0 ? padSize : length;
            return StringUtils.repeat('*', padSiz2);
        }
        int toIndex = length - lastSize;
        int n = padSiz = padSize > 0 ? padSize : toIndex - fromIndex;
        if (fromIndex == 0) {
            String tail = str.substring(toIndex);
            return StringUtils.repeat(padChar, padSiz).concat(tail);
        }
        if (toIndex == length) {
            String head = str.substring(0, fromIndex);
            return head.concat(StringUtils.repeat(padChar, padSiz));
        }
        String head = str.substring(0, fromIndex);
        String tail = str.substring(toIndex);
        return head + StringUtils.repeat(padChar, padSiz) + tail;
    }
}

