/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.core.convert.support.GenericConversionService
 */
package com.kuma.boot.common.utils.convert;

import com.kuma.boot.common.utils.common.RegexUtils;
import com.kuma.boot.common.utils.common.StringUtils;
import com.kuma.boot.common.utils.convert.ConversionService;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.reflect.ClassUtils;
import java.math.BigDecimal;
import org.jspecify.annotations.Nullable;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.GenericConversionService;

public class ConvertUtils {
    public static final Integer ZERO = 0;

    public static <T> @Nullable T convert(@Nullable Object source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        if (ClassUtils.isAssignableValue(targetType, (Object)source)) {
            return (T)source;
        }
        GenericConversionService conversionService = ConversionService.getInstance();
        return (T)conversionService.convert(source, targetType);
    }

    public static <T> @Nullable T convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        GenericConversionService conversionService = ConversionService.getInstance();
        return (T)conversionService.convert(source, sourceType, targetType);
    }

    public static <T> @Nullable T convert(@Nullable Object source, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        GenericConversionService conversionService = ConversionService.getInstance();
        return (T)conversionService.convert(source, targetType);
    }

    public static int convertInt(Object obj) {
        int value = ZERO;
        try {
            if (!ConvertUtils.dataNumberType(obj)) {
                return value;
            }
            value = Integer.valueOf(obj.toString());
        }
        catch (Exception ex) {
            LogUtils.error(ex.getCause(), ex.getMessage(), new Object[0]);
        }
        return value;
    }

    public static Float convertFloat(Object obj) {
        Float value = Float.valueOf(ZERO.intValue());
        try {
            if (!ConvertUtils.dataNumberType(obj)) {
                return value;
            }
            value = Float.valueOf(obj.toString());
        }
        catch (Exception ex) {
            LogUtils.error(ex.getCause(), ex.getMessage(), new Object[0]);
        }
        return value;
    }

    public static Double convertDouble(Object obj) {
        Double value = (double)ZERO;
        try {
            if (!ConvertUtils.dataNumberType(obj)) {
                return value;
            }
            value = Double.valueOf(obj.toString());
        }
        catch (Exception ex) {
            LogUtils.error(ex.getCause(), ex.getMessage(), new Object[0]);
        }
        return value;
    }

    public static BigDecimal convertDecimal(Object obj) {
        BigDecimal value = BigDecimal.ZERO;
        try {
            value = BigDecimal.valueOf(ConvertUtils.convertDouble(obj));
        }
        catch (Exception ex) {
            LogUtils.error(ex.getCause(), ex.getMessage(), new Object[0]);
        }
        return value;
    }

    public static Long convertLong(Object obj) {
        Long value = (long)ZERO;
        try {
            if (!ConvertUtils.dataNumberType(obj)) {
                return value;
            }
            value = Long.valueOf(obj.toString());
        }
        catch (Exception ex) {
            LogUtils.error(ex.getCause(), ex.getMessage(), new Object[0]);
        }
        return value;
    }

    public static Boolean convertBoolean(Object obj) {
        Boolean value = false;
        try {
            if (ConvertUtils.dataNumberType(obj.toString())) {
                int intValue = ConvertUtils.convertInt(obj);
                return intValue > ZERO;
            }
            value = Boolean.valueOf(obj.toString());
        }
        catch (Exception ex) {
            LogUtils.error(ex.getCause(), ex.getMessage(), new Object[0]);
        }
        return value;
    }

    public static String convertString(Object obj) {
        if (StringUtils.isNullAndSpaceOrEmpty(obj)) {
            return "";
        }
        return String.valueOf(obj);
    }

    private static boolean dataNumberType(Object obj) {
        if (StringUtils.isNullAndSpaceOrEmpty(obj)) {
            return false;
        }
        return RegexUtils.isNumber(String.valueOf(obj)) || RegexUtils.isFloatPoint(String.valueOf(obj));
    }
}

