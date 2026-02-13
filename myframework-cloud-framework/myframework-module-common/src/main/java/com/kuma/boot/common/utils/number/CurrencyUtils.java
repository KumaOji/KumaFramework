/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.number;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public final class CurrencyUtils {
    private static final int DEF_DIV_SCALE = 2;

    private CurrencyUtils() {
    }

    public static BigDecimal add(BigDecimal ... params) {
        BigDecimal result = BigDecimal.ZERO;
        for (BigDecimal param : params) {
            result = result.add(param).setScale(2, RoundingMode.HALF_UP);
        }
        return result;
    }

    public static BigDecimal sub(BigDecimal ... params) {
        BigDecimal result = params[0];
        for (BigDecimal param : params = (BigDecimal[])Arrays.stream(params).skip(1L).toArray()) {
            result = result.subtract(param).setScale(2, RoundingMode.HALF_UP);
        }
        return result;
    }

    public static BigDecimal mul(BigDecimal v1, BigDecimal v2) {
        return v1.multiply(v2).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal mul(BigDecimal v1, Integer v2) {
        return v1.multiply(BigDecimal.valueOf(v2.intValue())).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal mul(Integer v1, BigDecimal v2) {
        BigDecimal b1 = BigDecimal.valueOf(v1.intValue());
        return b1.multiply(v2).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal mul(BigDecimal v1, BigDecimal v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        return v1.multiply(v2).setScale(scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal mul(int v1, BigDecimal v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        return BigDecimal.valueOf(v1).multiply(v2).setScale(scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal div(BigDecimal v1, BigDecimal v2) {
        return v1.divide(v2, 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal div(BigDecimal v1, Integer v2) {
        return v1.divide(BigDecimal.valueOf(v2.intValue()), 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal div(BigDecimal v1, BigDecimal v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        if (v2.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        return v1.divide(v2, scale, RoundingMode.HALF_UP);
    }

    public static Integer fen(BigDecimal money) {
        BigDecimal price = CurrencyUtils.mul(money, BigDecimal.valueOf(100L));
        return price.intValue();
    }

    public static BigDecimal reversalFen(BigDecimal money) {
        return CurrencyUtils.div(money, BigDecimal.valueOf(100L));
    }
}

