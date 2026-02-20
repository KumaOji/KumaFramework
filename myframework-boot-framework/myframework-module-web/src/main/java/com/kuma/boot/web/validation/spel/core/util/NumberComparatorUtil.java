/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.core.util;

import java.math.BigDecimal;
import java.util.OptionalInt;

public class NumberComparatorUtil {
    public static final OptionalInt LESS_THAN = OptionalInt.of(-1);
    public static final OptionalInt FINITE_VALUE = OptionalInt.empty();
    public static final OptionalInt GREATER_THAN = OptionalInt.of(1);

    private NumberComparatorUtil() {
    }

    public static int compare(Number number, Number value, OptionalInt treatNanAs) {
        boolean valueIsDouble;
        if (number == null || value == null || treatNanAs == null || !treatNanAs.isPresent()) {
            throw new IllegalArgumentException("[Number], [Value] and [TreatNanAs] must not be null.");
        }
        if (number.equals(value)) {
            return 0;
        }
        boolean numberIsDouble = number instanceof Double || number instanceof Float;
        boolean bl = valueIsDouble = value instanceof Double || value instanceof Float;
        if (numberIsDouble && valueIsDouble) {
            return NumberComparatorUtil.compare(number.doubleValue(), value.doubleValue());
        }
        if (numberIsDouble) {
            return NumberComparatorUtil.compare(number.doubleValue(), value, treatNanAs);
        }
        if (valueIsDouble) {
            return NumberComparatorUtil.compare(number, value.doubleValue(), treatNanAs);
        }
        return BigDecimalUtil.valueOf(number).compareTo(BigDecimalUtil.valueOf(value));
    }

    private static int compare(Double number, double value) {
        return number.compareTo(value);
    }

    private static int compare(Number number, Double value, OptionalInt treatNanAs) {
        OptionalInt infinity = NumberComparatorUtil.infinityCheck(value, treatNanAs = OptionalInt.of(-treatNanAs.getAsInt()));
        if (infinity.isPresent()) {
            return -infinity.getAsInt();
        }
        return BigDecimalUtil.valueOf(number).compareTo(BigDecimal.valueOf(value));
    }

    private static int compare(Double number, Number value, OptionalInt treatNanAs) {
        OptionalInt infinity = NumberComparatorUtil.infinityCheck(number, treatNanAs);
        if (infinity.isPresent()) {
            return infinity.getAsInt();
        }
        return BigDecimalUtil.valueOf(number).compareTo(BigDecimalUtil.valueOf(value));
    }

    private static OptionalInt infinityCheck(Double number, OptionalInt treatNanAs) {
        OptionalInt result = FINITE_VALUE;
        if (number == Double.NEGATIVE_INFINITY) {
            result = LESS_THAN;
        } else if (number.isNaN()) {
            result = treatNanAs;
        } else if (number == Double.POSITIVE_INFINITY) {
            result = GREATER_THAN;
        }
        return result;
    }
}

