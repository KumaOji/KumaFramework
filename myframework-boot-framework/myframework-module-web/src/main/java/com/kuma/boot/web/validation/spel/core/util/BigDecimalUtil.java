/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.core.util;

import com.kuma.boot.web.validation.spel.core.exception.SpelArgumentException;
import java.math.BigDecimal;

public class BigDecimalUtil {
    private BigDecimalUtil() {
    }

    public static BigDecimal valueOf(Object val) {
        try {
            if (val instanceof BigDecimal) {
                return (BigDecimal)val;
            }
            if (val instanceof Double) {
                return BigDecimal.valueOf((Double)val);
            }
            if (val instanceof Float) {
                return BigDecimal.valueOf(((Float)val).floatValue());
            }
            return new BigDecimal(String.valueOf(val));
        }
        catch (NumberFormatException e) {
            throw new SpelArgumentException("Value [" + String.valueOf(val) + "] can not convert to BigDecimal.");
        }
    }
}

