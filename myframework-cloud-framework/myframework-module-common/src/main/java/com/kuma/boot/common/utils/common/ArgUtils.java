/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.common;

import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.math.BigDecimal;
import java.util.Collection;

public final class ArgUtils {
    private ArgUtils() {
    }

    public static void notNull(Object object, String name) {
        if (null == object) {
            throw new IllegalArgumentException(name + " can not be null!");
        }
    }

    public static void notNull(Object object, String name, String errMsg) {
        if (null == object) {
            String errorInfo = String.format("%s %s", name, errMsg);
            throw new IllegalArgumentException(errorInfo);
        }
    }

    public static void notEmpty(String string, String name) {
        if (StringUtils.isEmpty(string)) {
            throw new IllegalArgumentException(name + " can not be null!");
        }
    }

    public static void equals(Object except, Object real, String msg) {
        if (ObjectUtils.isNotEquals(except, real)) {
            String errorMsg = ArgUtils.buildErrorMsg(except, real, msg);
            throw new IllegalArgumentException(errorMsg);
        }
    }

    public static boolean isEqualsLen(String string, int len) {
        if (StringUtils.isEmpty(string)) {
            return 0 == len;
        }
        return string.length() == len;
    }

    public static boolean isNotEqualsLen(String string, int len) {
        return !ArgUtils.isEqualsLen(string, len);
    }

    public static boolean isFitMaxLen(String string, int maxLen) {
        if (StringUtils.isEmpty(string)) {
            return 0 <= maxLen;
        }
        return string.length() <= maxLen;
    }

    public static boolean isNotFitMaxLen(String string, int maxLen) {
        return !ArgUtils.isFitMaxLen(string, maxLen);
    }

    public static boolean isFitMinLen(String string, int minLen) {
        if (StringUtils.isEmpty(string)) {
            return 0 >= minLen;
        }
        return string.length() >= minLen;
    }

    public static boolean isNotFitMinLen(String string, int minLen) {
        return !ArgUtils.isFitMinLen(string, minLen);
    }

    public static Boolean isNumber(String number) {
        if (ObjectUtils.isNotNull(number)) {
            try {
                new BigDecimal(number);
                return true;
            }
            catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    public static Boolean isNotNumber(String number) {
        return ArgUtils.isNumber(number) == false;
    }

    public static Boolean isMatchesRegex(String string, String regex) {
        if (null != string) {
            return string.matches(regex);
        }
        return true;
    }

    public static Boolean isNotMatchesRegex(String string, String regex) {
        return ArgUtils.isMatchesRegex(string, regex) == false;
    }

    private static String buildErrorMsg(Object except, Object real, String msg) {
        String resultMsg = msg;
        if (StringUtils.isEmpty(resultMsg)) {
            resultMsg = "\u4e0e\u671f\u671b\u503c\u4e0d\u7b26\u5408!";
        }
        return String.format("Except:<%s>, Real:<%s>, Msg:<%s>", except, real, resultMsg);
    }

    public static void positive(int number, String paramName) {
        if (number <= 0) {
            throw new IllegalArgumentException(paramName + " must be > 0!");
        }
    }

    public static void notNegative(int number, String paramName) {
        if (number < 0) {
            throw new IllegalArgumentException(paramName + " must be >= 0!");
        }
    }

    public static void positive(long number, String paramName) {
        if (number <= 0L) {
            throw new IllegalArgumentException(paramName + " must be > 0!");
        }
    }

    public static void notNegative(long number, String paramName) {
        if (number < 0L) {
            throw new IllegalArgumentException(paramName + " must be >= 0!");
        }
    }

    public static void positive(double number, String paramName) {
        if (number < 0.0) {
            throw new IllegalArgumentException(paramName + " must be > 0!");
        }
    }

    public static void notNegative(double number, String paramName) {
        if (number < 0.0) {
            throw new IllegalArgumentException(paramName + " must be >= 0!");
        }
    }

    public static void assertTrue(boolean condition, String name) {
        if (!condition) {
            throw new IllegalArgumentException(name + " excepted true but is false!");
        }
    }

    public static void assertFalse(boolean condition, String name) {
        if (condition) {
            throw new IllegalArgumentException(name + " excepted false but is true!");
        }
    }

    public static void notEmpty(Object[] array, String name) {
        if (ArrayUtils.isEmpty(array)) {
            throw new IllegalArgumentException(name + " excepted is not empty!");
        }
        for (Object object : array) {
            ArgUtils.notNull(object, name + " element ");
        }
    }

    public static void notEmpty(Collection<?> collection, String name) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new IllegalArgumentException(name + " excepted is not empty!");
        }
        for (Object object : collection) {
            ArgUtils.notNull(object, name + " element ");
        }
    }

    @Deprecated
    public static void gt(long actual, long expected) {
        ArgUtils.gt("", actual, expected);
    }

    public static void gt(String paramName, long actual, long expected) {
        if (actual > expected) {
            return;
        }
        throw new IllegalArgumentException("[" + paramName + "] actual is <" + actual + ">, expected is gt " + expected);
    }

    public static void gte(String paramName, long actual, long expected) {
        if (actual >= expected) {
            return;
        }
        throw new IllegalArgumentException("[" + paramName + "] actual is <" + actual + ">, expected is gte " + expected);
    }

    public static void lt(String paramName, long actual, long expected) {
        if (actual < expected) {
            return;
        }
        throw new IllegalArgumentException("[" + paramName + "] actual is <" + actual + ">, expected is lt " + expected);
    }

    public static void lte(String paramName, long actual, long expected) {
        if (actual <= expected) {
            return;
        }
        throw new IllegalArgumentException("[" + paramName + "] actual is <" + actual + ">, expected is lte " + expected);
    }
}

