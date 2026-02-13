/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.annotation.Nullable
 *  org.springframework.util.NumberUtils
 */
package com.kuma.boot.common.utils.number;

import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

public class NumberUtils
extends org.springframework.util.NumberUtils {
    public static final byte[] DIGITS = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90};
    private static final String[] hanArr = new String[]{"\u96f6", "\u4e00", "\u4e8c", "\u4e09", "\u56db", "\u4e94", "\u516d", "\u4e03", "\u516b", "\u4e5d"};
    private static final String[] unitArr = new String[]{"\u5341", "\u767e", "\u5343", "\u4e07", "\u5341", "\u767d", "\u5343", "\u4ebf", "\u5341", "\u767e", "\u5343"};

    private NumberUtils() {
    }

    public static int toInt(String str) {
        return ObjectUtils.toInt(str, 0);
    }

    public static int toInt(@Nullable String str, int defaultValue) {
        return ObjectUtils.toInt(str, defaultValue);
    }

    public static long toLong(String str) {
        return ObjectUtils.toLong(str, 0L);
    }

    public static long toLong(@Nullable String str, long defaultValue) {
        return ObjectUtils.toLong(str, defaultValue);
    }

    public static String to62Str(long i) {
        int radix = DIGITS.length;
        byte[] buf = new byte[65];
        int charPos = 64;
        for (i = -i; i <= (long)(-radix); i /= (long)radix) {
            buf[charPos--] = DIGITS[(int)(-(i % (long)radix))];
        }
        buf[charPos] = DIGITS[(int)(-i)];
        return new String(buf, charPos, 65 - charPos, StandardCharsets.UTF_8);
    }

    public static double scale(Number number, int scale) {
        if (Objects.nonNull(number)) {
            try {
                BigDecimal bg = BigDecimal.valueOf(number.doubleValue());
                return bg.setScale(scale, RoundingMode.HALF_UP).doubleValue();
            }
            catch (Exception e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    public static int stoi(String string) {
        return NumberUtils.stoi(string, 0);
    }

    public static int stoi(String string, int defaultValue) {
        int id;
        if (string == null || string.equalsIgnoreCase("") || string.equals("null")) {
            return defaultValue;
        }
        try {
            id = Integer.parseInt(string);
        }
        catch (NumberFormatException e) {
            LogUtils.error(e);
            return defaultValue;
        }
        return id;
    }

    public static long stol(String string) {
        return NumberUtils.stol(string, 0L);
    }

    public static long stol(String string, long defaultValue) {
        long ret;
        if (string == null || string.equalsIgnoreCase("")) {
            return defaultValue;
        }
        try {
            ret = Long.parseLong(string);
        }
        catch (NumberFormatException e) {
            LogUtils.error(e);
            return defaultValue;
        }
        return ret;
    }

    public static double stod(String string) {
        return NumberUtils.stod(string, 0.0);
    }

    public static double stod(String string, double defaultValue) {
        double ret;
        if (string == null || string.equalsIgnoreCase("")) {
            return defaultValue;
        }
        try {
            ret = Double.parseDouble(string);
        }
        catch (NumberFormatException e) {
            LogUtils.error(e);
            return defaultValue;
        }
        return ret;
    }

    public static String toChineseNum(int number) {
        String numStr = String.valueOf(number);
        StringBuilder result = new StringBuilder();
        int numLen = numStr.length();
        for (int i = 0; i < numLen; ++i) {
            int num = numStr.charAt(i) - 48;
            if (i != numLen - 1 && num != 0) {
                result.append(hanArr[num]).append(unitArr[numLen - 2 - i]);
                if (number < 10 || number >= 20) continue;
                result = new StringBuilder(result.substring(1));
                continue;
            }
            if (number >= 10 && number % 10 == 0) continue;
            result.append(hanArr[num]);
        }
        return result.toString();
    }

    public static int random(int min, int max) {
        return new Random().nextInt(max - min) + min;
    }

    public static int compare(BigDecimal flowPrice, BigDecimal actualRefundPrice) {
        return flowPrice.compareTo(actualRefundPrice);
    }

    private static <T> Optional<T> parseNumber(String str, Predicate<String> validateFn, Function<String, T> parseFn) {
        Objects.requireNonNull(validateFn);
        Objects.requireNonNull(parseFn);
        if (StringUtils.isEmpty(str)) {
            return Optional.empty();
        }
        if ((str = str.trim()).length() == 0) {
            return Optional.empty();
        }
        if (!validateFn.test(str)) {
            return Optional.empty();
        }
        return Optional.of(parseFn.apply(str));
    }

    public static Optional<Long> parseLong(String value) {
        return NumberUtils.parseNumber(value, NumberUtils::isInteger, Long::parseLong);
    }

    public static Optional<Integer> parseInteger(String value) {
        return NumberUtils.parseLong(value).map(Math::toIntExact);
    }

    public static Optional<BigInteger> parseBigInteger(String value) {
        return NumberUtils.parseNumber(value, NumberUtils::isInteger, BigInteger::new);
    }

    public static Optional<Double> parseDouble(String value) {
        return NumberUtils.parseNumber(value, NumberUtils::isDecimal, Double::parseDouble);
    }

    public static Optional<Float> parseFloat(String value) {
        return NumberUtils.parseNumber(value, NumberUtils::isDecimal, Float::parseFloat);
    }

    public static Optional<BigDecimal> parseBigDecimal(String value) {
        return NumberUtils.parseNumber(value, NumberUtils::isDecimal, BigDecimal::new);
    }

    private static boolean isInteger(String str) {
        int len = str.length();
        for (int i = 0; i < len; ++i) {
            char ch = str.charAt(i);
            if (Character.isDigit(ch) || i == 0 && ch == '-') continue;
            return false;
        }
        return true;
    }

    private static boolean isDecimal(String str) {
        int len = str.length();
        boolean point = false;
        for (int i = 0; i < len; ++i) {
            char ch = str.charAt(i);
            if (Character.isDigit(ch) || i == 0 && ch == '-') continue;
            if (i > 0 && ch == '.' && !point) {
                point = true;
                continue;
            }
            return false;
        }
        return true;
    }
}

