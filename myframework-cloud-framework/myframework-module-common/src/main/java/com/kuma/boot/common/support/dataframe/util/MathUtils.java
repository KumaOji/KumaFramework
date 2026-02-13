/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MathUtils {
    public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    private MathUtils() {
    }

    public static BigDecimal percentage(BigDecimal dividend) {
        if (dividend == null) {
            return null;
        }
        return dividend.multiply(ONE_HUNDRED).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal percentage(BigDecimal dividend, int scale) {
        if (dividend == null) {
            return null;
        }
        return dividend.multiply(ONE_HUNDRED).setScale(scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal proportion(BigDecimal dividend, BigDecimal divisor, int scale) {
        if (dividend == null || divisor == null || divisor.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return dividend.divide(divisor, 8, RoundingMode.HALF_UP).multiply(ONE_HUNDRED).setScale(scale, RoundingMode.HALF_UP);
    }

    public static <T1, T2> BigDecimal proportion(T1 dividend, T2 divisor, int scale) {
        if (dividend == null || divisor == null) {
            return null;
        }
        BigDecimal dividendBig = MathUtils.toBigDecimal(dividend);
        BigDecimal divisorBig = MathUtils.toBigDecimal(divisor);
        if (divisorBig.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return MathUtils.proportion(dividendBig, divisorBig, scale);
    }

    public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor, int scale) {
        if (dividend == null || divisor == null || divisor.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return dividend.divide(divisor, 8, RoundingMode.HALF_UP).setScale(scale, RoundingMode.HALF_UP);
    }

    public static <T1, T2> BigDecimal divide(T1 dividend, T2 divisor, int scale) {
        return MathUtils.divide(dividend, divisor, scale, RoundingMode.HALF_UP);
    }

    public static <T1, T2> BigDecimal divide(T1 dividend, T2 divisor, int scale, RoundingMode roundingMode) {
        if (dividend == null || divisor == null) {
            return null;
        }
        BigDecimal dividendBig = MathUtils.toBigDecimal(dividend);
        BigDecimal divisorBig = MathUtils.toBigDecimal(divisor);
        if (divisorBig.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return dividendBig.divide(divisorBig, scale, roundingMode);
    }

    public static <T> BigDecimal toBigDecimal(T value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal)value;
        }
        if (value instanceof Integer) {
            return new BigDecimal((Integer)value);
        }
        return new BigDecimal(String.valueOf(value));
    }

    public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
        return MathUtils.divide(dividend, divisor, 8);
    }

    public static BigDecimal calcGrowthRate(BigDecimal nowValue, BigDecimal preValue, int scale) {
        return MathUtils.doCalcGrowthRate(nowValue, preValue, scale, true);
    }

    public static BigDecimal calcGrowthRateNoMulti100(BigDecimal nowValue, BigDecimal preValue, int scale) {
        return MathUtils.doCalcGrowthRate(nowValue, preValue, scale, false);
    }

    public static BigDecimal doCalcGrowthRate(BigDecimal nowValue, BigDecimal preValue, int scale, boolean multi100) {
        if (nowValue == null || preValue == null || preValue.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        BigDecimal subtract = nowValue.subtract(preValue);
        BigDecimal growRate = subtract.divide(preValue, 8, 4);
        if (multi100) {
            growRate = growRate.multiply(ONE_HUNDRED);
        }
        return growRate.setScale(scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal calcVolatilityRate(List<BigDecimal> valuesList) {
        BigDecimal avg = MathUtils.avg(valuesList);
        List<BigDecimal> dispersionRateList = valuesList.stream().map(e -> e.subtract(avg).divide(avg, 8, 4).abs()).collect(Collectors.toList());
        BigDecimal result = MathUtils.avg(dispersionRateList);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            result = result.abs();
        }
        result = result.multiply(ONE_HUNDRED).setScale(2, RoundingMode.HALF_UP);
        return result;
    }

    public static BigDecimal average(List<BigDecimal> bigDecimals, RoundingMode roundingMode, int scale) {
        BigDecimal sum = bigDecimals.stream().map(Objects::requireNonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(new BigDecimal(bigDecimals.size()), roundingMode).setScale(scale, roundingMode);
    }

    private static BigDecimal avg(List<BigDecimal> valuesList) {
        return valuesList.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(valuesList.size()), 8, RoundingMode.HALF_UP);
    }
}

