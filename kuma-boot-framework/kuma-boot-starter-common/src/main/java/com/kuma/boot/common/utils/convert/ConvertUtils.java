/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.utils.convert;

import static com.kuma.boot.common.utils.common.StringUtils.isNullAndSpaceOrEmpty;

import com.kuma.boot.common.utils.common.RegexUtils;
import com.kuma.boot.common.utils.common.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.reflect.ClassUtils;
import java.math.BigDecimal;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.GenericConversionService;
import org.jspecify.annotations.Nullable;

/**
 * 基于 spring ConversionService 类型转换
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 19:41:13
 */
public class ConvertUtils {

    /**
     * 零
     */
    public static final Integer ZERO = 0;

    /**
     * Convenience operation for converting a source object to the specified targetType.
     * {@link TypeDescriptor#forObject(Object)}.
     * @param source the source object
     * @param targetType the target type
     * @param <T> 泛型标记
     * @return the converted value
     * @throws IllegalArgumentException if targetType is {@code null}, or sourceType is
     * {@code null} but source is not {@code null}
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T convert(@Nullable Object source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        if (ClassUtils.isAssignableValue(targetType, source)) {
            return (T) source;
        }
        GenericConversionService conversionService = ConversionService.getInstance();
        return conversionService.convert(source, targetType);
    }

    /**
     * Convenience operation for converting a source object to the specified targetType,
     * where the target type is a descriptor that provides additional conversion context.
     * {@link TypeDescriptor#forObject(Object)}.
     * @param source the source object
     * @param sourceType the source type
     * @param targetType the target type
     * @param <T> 泛型标记
     * @return the converted value
     * @throws IllegalArgumentException if targetType is {@code null}, or sourceType is
     * {@code null} but source is not {@code null}
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T convert(
            @Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        GenericConversionService conversionService = ConversionService.getInstance();
        return (T) conversionService.convert(source, sourceType, targetType);
    }

    /**
     * Convenience operation for converting a source object to the specified targetType,
     * where the target type is a descriptor that provides additional conversion context.
     * Simply delegates to {@link #convert(Object, TypeDescriptor, TypeDescriptor)} and
     * encapsulates the construction of the source type descriptor using
     * {@link TypeDescriptor#forObject(Object)}.
     * @param source the source object
     * @param targetType the target type
     * @param <T> 泛型标记
     * @return the converted value
     * @throws IllegalArgumentException if targetType is {@code null}, or sourceType is
     * {@code null} but source is not {@code null}
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T convert(@Nullable Object source, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        GenericConversionService conversionService = ConversionService.getInstance();
        return (T) conversionService.convert(source, targetType);
    }

    /**
     * 将值转换为整数
     * @param obj 值
     * @return 返回结果
     */
    public static int convertInt(Object obj) {
        int value = ZERO;
        try {
            // 验证是否为数字
            if (!dataNumberType(obj)) {
                return value;
            }

            value = Integer.valueOf(obj.toString());
        } catch (Exception ex) {
            LogUtils.error(ex.getCause(), ex.getMessage());
        }
        // 返回结果
        return value;
    }

    /**
     * 将值转换为浮动
     * @param obj 值
     * @return 返回结果
     */
    public static Float convertFloat(Object obj) {
        Float value = Float.valueOf(ZERO);
        try {
            // 验证是否为数字
            if (!dataNumberType(obj)) {
                return value;
            }

            value = Float.valueOf(obj.toString());
        } catch (Exception ex) {
            LogUtils.error(ex.getCause(), ex.getMessage());
        }
        // 返回结果
        return value;
    }

    /**
     * 将值转换为双精度
     * @param obj 值
     * @return 返回结果
     */
    public static Double convertDouble(Object obj) {
        Double value = Double.valueOf(ZERO);
        try {
            // 验证是否为数字
            if (!dataNumberType(obj)) {
                return value;
            }

            value = Double.valueOf(obj.toString());
        } catch (Exception ex) {
            LogUtils.error(ex.getCause(), ex.getMessage());
        }
        // 返回结果
        return value;
    }

    /**
     * 将值转换为大十进制
     * @param obj 值
     * @return 返回结果
     */
    public static BigDecimal convertDecimal(Object obj) {
        BigDecimal value = BigDecimal.ZERO;
        try {
            value = BigDecimal.valueOf(convertDouble(obj));
        } catch (Exception ex) {
            LogUtils.error(ex.getCause(), ex.getMessage());
        }
        // 返回结果
        return value;
    }

    /**
     * 转换为长整形
     * @param obj 数据
     * @return 返回结果
     */
    public static Long convertLong(Object obj) {
        Long value = Long.valueOf(ZERO);
        try {
            // 验证是否为数字
            if (!dataNumberType(obj)) {
                return value;
            }

            value = Long.valueOf(obj.toString());
        } catch (Exception ex) {
            LogUtils.error(ex.getCause(), ex.getMessage());
        }
        // 返回结果
        return value;
    }

    /**
     * 将值转换为布尔
     * @param obj 值
     * @return 返回结果
     */
    public static Boolean convertBoolean(Object obj) {
        Boolean value = false;
        try {
            // 验证是否为数字
            if (dataNumberType(obj.toString())) {
                int intValue = convertInt(obj);
                return intValue > ZERO;
            }
            // 直接转换
            value = Boolean.valueOf(obj.toString());
        } catch (Exception ex) {
            LogUtils.error(ex.getCause(), ex.getMessage());
        }
        // 返回结果
        return value;
    }

    /**
     * 转换为字段串类型
     * @param obj 数据
     * @return 返回结果
     */
    public static String convertString(Object obj) {
        if (isNullAndSpaceOrEmpty(obj)) {
            return StringUtils.Empty;
        }

        // 返回结果
        return String.valueOf(obj);
    }

    /**
     * 数据类型判断
     * @param obj 值
     * @return 返回结果
     */
    private static boolean dataNumberType(Object obj) {
        // 是否为空
        if (isNullAndSpaceOrEmpty(obj)) {
            return false;
        }
        // 验证是否为数字
        return RegexUtils.isNumber(String.valueOf(obj))
                || RegexUtils.isFloatPoint(String.valueOf(obj));
    }
}
