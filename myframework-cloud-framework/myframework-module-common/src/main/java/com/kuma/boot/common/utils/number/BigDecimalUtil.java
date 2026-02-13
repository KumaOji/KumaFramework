/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.number;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class BigDecimalUtil {
    public static final int CURRENCY_DECIMAL_PLACES = 2;

    public static BigDecimal add(BigDecimal first, BigDecimal ... lastArgs) {
        int argsLength = lastArgs.length;
        BigDecimal result = new BigDecimal("0.00");
        if (first != null) {
            result = result.add(first);
        }
        for (int i = 0; i < argsLength; ++i) {
            lastArgs[i] = lastArgs[i] == null ? new BigDecimal("0.00") : lastArgs[i];
            result = result.add(lastArgs[i]);
        }
        result = result.setScale(2, RoundingMode.UP);
        return result;
    }

    public static BigDecimal subtract(BigDecimal first, BigDecimal ... lastArgs) {
        int argsLength = lastArgs.length;
        BigDecimal result = new BigDecimal("0.00");
        if (first != null) {
            result = result.add(first);
        }
        for (int i = 0; i < argsLength; ++i) {
            lastArgs[i] = lastArgs[i] == null ? new BigDecimal("0.00") : lastArgs[i];
            result = result.subtract(lastArgs[i]);
        }
        result = result.setScale(2, RoundingMode.UP);
        return result;
    }

    public static BigDecimal multiply(BigDecimal first, BigDecimal ... lastArgs) {
        int argsLength = lastArgs.length;
        BigDecimal result = new BigDecimal("0.00");
        if (first != null) {
            result = result.add(first);
        }
        for (int i = 0; i < argsLength; ++i) {
            if (result.compareTo(new BigDecimal("0.00")) == 0) {
                return result;
            }
            lastArgs[i] = lastArgs[i] == null ? new BigDecimal("0.00") : lastArgs[i];
            result = result.multiply(lastArgs[i]);
        }
        result = result.setScale(2, RoundingMode.UP);
        return result;
    }

    public static BigDecimal divide(BigDecimal first, BigDecimal ... lastArgs) {
        int argsLength = lastArgs.length;
        BigDecimal result = new BigDecimal("0.00");
        if (first != null) {
            result = result.add(first);
        }
        for (int i = 0; i < argsLength; ++i) {
            if (result.compareTo(new BigDecimal("0.00")) == 0) {
                return result;
            }
            lastArgs[i] = lastArgs[i] == null ? new BigDecimal("1.00") : lastArgs[i];
            result = result.divide(lastArgs[i], 2, RoundingMode.UP);
        }
        result = result.setScale(2, RoundingMode.UP);
        return result;
    }

    public static int compareTo(BigDecimal first, BigDecimal last) {
        BigDecimal newFirst = BigDecimal.ZERO;
        BigDecimal newLast = BigDecimal.ZERO;
        if (first != null) {
            newFirst = first;
        }
        if (last != null) {
            newLast = last;
        }
        return newFirst.compareTo(newLast);
    }

    public static BigDecimal getZero() {
        BigDecimal result = new BigDecimal("0.00");
        result = result.setScale(2, RoundingMode.UP);
        return result;
    }

    public static BigDecimal convertStringToBigDecimal(String first) {
        return BigDecimal.valueOf(Double.parseDouble(first)).setScale(2, RoundingMode.UP);
    }

    public static String toString(BigDecimal first) {
        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.US);
        if (first == null) {
            return currency.format(new BigDecimal("0.00"));
        }
        return currency.format(first);
    }

    public static <E> Map<E, BigDecimal> averageNumber(BigDecimal totalNumber, Map<E, BigDecimal> items) {
        return BigDecimalUtil.averageNumber(totalNumber, items, 2, RoundingMode.UP);
    }

    public static <E> Map<E, BigDecimal> averageNumber(BigDecimal totalNumber, Map<E, BigDecimal> items, int scale, RoundingMode roundingMode) {
        BigDecimal number = items.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remainderNumber = totalNumber;
        LinkedHashMap<E, BigDecimal> result = new LinkedHashMap<E, BigDecimal>();
        int i = 1;
        for (Map.Entry<E, BigDecimal> entry : items.entrySet()) {
            if (i == items.entrySet().size()) {
                result.put(entry.getKey(), remainderNumber);
                break;
            }
            BigDecimal value = BigDecimal.ZERO;
            if (BigDecimal.ZERO.compareTo(number) != 0) {
                value = totalNumber.multiply(entry.getValue()).divide(number, scale, roundingMode);
            }
            result.put(entry.getKey(), value);
            remainderNumber = remainderNumber.subtract(value);
            ++i;
        }
        return result;
    }
}

