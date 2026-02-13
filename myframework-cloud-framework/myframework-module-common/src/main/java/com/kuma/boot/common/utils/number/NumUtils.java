/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.number;

import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.utils.collection.ArrayPrimitiveUtils;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.reflect.ClassTypeUtils;
import com.kuma.boot.common.utils.reflect.PrimitiveUtils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Optional;

public final class NumUtils {
    public static final char[] HEX_CHARS = "1234567890abcdefABCDEF".toCharArray();

    private NumUtils() {
    }

    public static int getMin(int current, int other) {
        return Math.min(current, other);
    }

    public static int getMax(int current, int other) {
        return Math.max(current, other);
    }

    public static Optional<Integer> toInteger(String string) {
        if (StringUtils.isEmpty(string)) {
            return Optional.empty();
        }
        try {
            Integer integer = Integer.valueOf(string);
            return Optional.of(integer);
        }
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Integer toIntegerThrows(String string) {
        ArgUtils.notEmpty(string, "string");
        try {
            return Integer.valueOf(string);
        }
        catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public static int toInteger(String string, int defaultValue) {
        try {
            return Integer.parseInt(string);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    public static Optional<Long> toLong(String string) {
        if (StringUtils.isEmpty(string)) {
            return Optional.empty();
        }
        try {
            Long aLong = Long.valueOf(string);
            return Optional.of(aLong);
        }
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Double> toDouble(String string) {
        if (StringUtils.isEmpty(string)) {
            return Optional.empty();
        }
        try {
            Double aDouble = Double.valueOf(string);
            return Optional.of(aDouble);
        }
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static boolean isHex(String string) {
        char[] chars;
        if (StringUtils.isEmpty(string)) {
            return false;
        }
        for (char c : chars = string.toCharArray()) {
            if (ArrayPrimitiveUtils.indexOf(HEX_CHARS, c) >= 0) continue;
            return false;
        }
        return true;
    }

    public static Long parseLong(Object object) {
        if (ObjectUtils.isNull(object)) {
            return null;
        }
        Class<?> valueClass = object.getClass();
        if (object instanceof Byte || valueClass == Byte.TYPE) {
            Byte aByte = (Byte)object;
            return aByte.longValue();
        }
        if (object instanceof Short || valueClass == Short.TYPE) {
            Short aShort = (Short)object;
            return aShort.longValue();
        }
        if (object instanceof Integer || valueClass == Integer.TYPE) {
            Integer integer = (Integer)object;
            return integer.longValue();
        }
        if (object instanceof Long) {
            return (Long)object;
        }
        if (object instanceof CharSequence) {
            CharSequence charSequence = (CharSequence)object;
            return Long.parseLong(charSequence.toString());
        }
        if (object instanceof BigInteger) {
            BigInteger bigInteger = (BigInteger)object;
            return bigInteger.longValue();
        }
        if (object instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal)object;
            return bigDecimal.longValue();
        }
        throw new ClassCastException("Class cast exception for parse long with object: " + String.valueOf(object));
    }

    public static String getNumFormat(Number number, String format) {
        ArgUtils.notNull(number, "number");
        ArgUtils.notEmpty(format, "format");
        DecimalFormat numberFormat = new DecimalFormat(format);
        return numberFormat.format(number);
    }

    public static <T> T getFormatNum(String number, String format, Class<T> numberClazz) {
        ArgUtils.notNull(number, "number");
        ArgUtils.notEmpty(format, "format");
        DecimalFormat numberFormat = new DecimalFormat(format);
        try {
            Number numValue = numberFormat.parse(number);
            if (BigDecimal.class == numberClazz) {
                return (T)BigDecimal.valueOf((Double)numValue);
            }
            if (BigInteger.class == numberClazz) {
                return (T)BigInteger.valueOf((Long)numValue);
            }
            if (Float.class == numberClazz || Float.TYPE == numberClazz) {
                // empty if block
            }
            return (T)numValue;
        }
        catch (ParseException e) {
            throw new BootException(e);
        }
    }

    public static Number getFormatNum(String number, String format) {
        return NumUtils.getFormatNum(number, format, Number.class);
    }

    public static Object getFormatNumCast(String numberStr, String format, Class numberClazz) {
        ArgUtils.notEmpty(numberStr, "numberStr");
        ArgUtils.notEmpty(format, "format");
        ArgUtils.notNull(numberClazz, "numberClazz");
        Class actualClazz = numberClazz;
        if (ClassTypeUtils.isPrimitive(actualClazz)) {
            actualClazz = PrimitiveUtils.getReferenceType(numberClazz);
        }
        DecimalFormat numberFormat = new DecimalFormat(format);
        try {
            Number numValue = numberFormat.parse(numberStr);
            if (Integer.class == actualClazz) {
                return numValue.intValue();
            }
            if (Long.class == actualClazz) {
                return numValue.longValue();
            }
            if (Float.class == actualClazz) {
                return Float.valueOf(numValue.floatValue());
            }
            if (Double.class == actualClazz) {
                return numValue.doubleValue();
            }
            if (Short.class == actualClazz) {
                return numValue.shortValue();
            }
            if (Byte.class == actualClazz) {
                return numValue.byteValue();
            }
            if (BigDecimal.class == actualClazz) {
                return BigDecimal.valueOf((Double)numValue);
            }
            if (BigInteger.class == actualClazz) {
                return BigInteger.valueOf((Long)numValue);
            }
            return numValue;
        }
        catch (ParseException e) {
            throw new BootException(e);
        }
    }

    public static BigInteger toBigInteger(BigDecimal bigDecimal) {
        if (null == bigDecimal) {
            return null;
        }
        return bigDecimal.toBigInteger();
    }

    public static BigDecimal parseBigDecimal(BigInteger bigInteger) {
        if (null == bigInteger) {
            return null;
        }
        return new BigDecimal(bigInteger);
    }
}

