/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.ObjectUtil
 *  org.jspecify.annotations.Nullable
 *  org.springframework.util.ObjectUtils
 */
package com.kuma.boot.common.utils.lang;

import cn.hutool.core.util.ObjectUtil;
import com.kuma.boot.common.support.handler.Handler;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.collection.MapUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.reflect.ClassTypeUtils;
import com.kuma.boot.common.utils.reflect.ClassUtils;
import com.kuma.boot.common.utils.reflect.ReflectFieldUtils;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import org.jspecify.annotations.Nullable;

public final class ObjectUtils
extends org.springframework.util.ObjectUtils {
    public static boolean isTrue(@Nullable Boolean object) {
        return Boolean.TRUE.equals(object);
    }

    public static boolean isFalse(@Nullable Boolean object) {
        return object == null || Boolean.FALSE.equals(object);
    }

    public static boolean isNotEmpty(@Nullable Object[] array) {
        return !ObjectUtils.isEmpty((Object[])array);
    }

    public static boolean equals(@Nullable Object o1, @Nullable Object o2) {
        return Objects.equals(o1, o2);
    }

    public static boolean isNotEqual(Object o1, Object o2) {
        return !Objects.equals(o1, o2);
    }

    public static int hashCode(@Nullable Object obj) {
        return Objects.hashCode(obj);
    }

    public static Object defaultIfNull(@Nullable Object object, Object defaultValue) {
        return object != null ? object : defaultValue;
    }

    public static @Nullable String toStr(@Nullable Object object) {
        return ObjectUtils.toStr(object, null);
    }

    public static @Nullable String toStr(@Nullable Object object, @Nullable String defaultValue) {
        if (null == object) {
            return defaultValue;
        }
        if (object instanceof CharSequence) {
            return ((CharSequence)object).toString();
        }
        return String.valueOf(object);
    }

    public static int toInt(@Nullable Object object) {
        return ObjectUtils.toInt(object, 0);
    }

    public static int toInt(@Nullable Object object, int defaultValue) {
        if (object instanceof Number) {
            return ((Number)object).intValue();
        }
        if (object instanceof CharSequence) {
            String value = ((CharSequence)object).toString();
            try {
                return Integer.parseInt(value);
            }
            catch (NumberFormatException nfe) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static long toLong(@Nullable Object object) {
        return ObjectUtils.toLong(object, 0L);
    }

    public static long toLong(@Nullable Object object, long defaultValue) {
        if (object instanceof Number) {
            return ((Number)object).longValue();
        }
        if (object instanceof CharSequence) {
            String value = ((CharSequence)object).toString();
            try {
                return Long.parseLong(value);
            }
            catch (NumberFormatException nfe) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static float toFloat(@Nullable Object object) {
        return ObjectUtils.toFloat(object, 0.0f);
    }

    public static float toFloat(@Nullable Object object, float defaultValue) {
        if (object instanceof Number) {
            return ((Number)object).floatValue();
        }
        if (object instanceof CharSequence) {
            String value = ((CharSequence)object).toString();
            try {
                return Float.parseFloat(value);
            }
            catch (NumberFormatException nfe) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static double toDouble(@Nullable Object object) {
        return ObjectUtils.toDouble(object, 0.0);
    }

    public static double toDouble(@Nullable Object object, double defaultValue) {
        if (object instanceof Number) {
            return ((Number)object).doubleValue();
        }
        if (object instanceof CharSequence) {
            String value = ((CharSequence)object).toString();
            try {
                return Double.parseDouble(value);
            }
            catch (NumberFormatException nfe) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static byte toByte(@Nullable Object object) {
        return ObjectUtils.toByte(object, (byte)0);
    }

    public static byte toByte(@Nullable Object object, byte defaultValue) {
        if (object instanceof Number) {
            return ((Number)object).byteValue();
        }
        if (object instanceof CharSequence) {
            String value = ((CharSequence)object).toString();
            try {
                return Byte.parseByte(value);
            }
            catch (NumberFormatException nfe) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static short toShort(@Nullable Object object) {
        return ObjectUtils.toShort(object, (short)0);
    }

    public static short toShort(@Nullable Object object, short defaultValue) {
        if (object instanceof Number) {
            return ((Number)object).byteValue();
        }
        if (object instanceof CharSequence) {
            String value = ((CharSequence)object).toString();
            try {
                return Short.parseShort(value);
            }
            catch (NumberFormatException nfe) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static @Nullable Boolean toBoolean(@Nullable Object object) {
        return ObjectUtils.toBoolean(object, null);
    }

    public static @Nullable Boolean toBoolean(@Nullable Object object, @Nullable Boolean defaultValue) {
        if (object instanceof Boolean) {
            return (Boolean)object;
        }
        if (object instanceof CharSequence) {
            String value = ((CharSequence)object).toString();
            if ("true".equalsIgnoreCase(value) || "y".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value)) {
                return true;
            }
            if ("false".equalsIgnoreCase(value) || "n".equalsIgnoreCase(value) || "no".equalsIgnoreCase(value) || "off".equalsIgnoreCase(value) || "0".equalsIgnoreCase(value)) {
                return false;
            }
        }
        return defaultValue;
    }

    public static <R> List<R> toList(Object object, Handler<Object, R> handler) {
        if (ObjectUtils.isNull(object)) {
            return Collections.emptyList();
        }
        Class<?> clazz = object.getClass();
        if (ClassTypeUtils.isCollection(clazz)) {
            Collection collection = (Collection)object;
            return CollectionUtils.toList(collection, handler);
        }
        if (clazz.isArray()) {
            return ArrayUtils.toList(object, handler);
        }
        throw new UnsupportedOperationException("Not support foreach() for class: " + clazz.getName());
    }

    public static void emptyToNull(Object object) {
        if (null == object) {
            return;
        }
        List<Field> fieldList = ClassUtils.getAllFieldList(object.getClass());
        for (Field field : fieldList) {
            Object value = ReflectFieldUtils.getValue(field, object);
            if (!ObjectUtils.isEmpty(value)) continue;
            ReflectFieldUtils.setValue(field, object, null);
        }
    }

    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        Map<String, Field> sourceFieldMap = ClassUtils.getAllFieldMap(source.getClass());
        Map<String, Field> targetFieldMap = ClassUtils.getAllFieldMap(target.getClass());
        for (Map.Entry<String, Field> entry : sourceFieldMap.entrySet()) {
            String sourceFieldName = entry.getKey();
            Field sourceField = entry.getValue();
            Field targetField = targetFieldMap.get(sourceFieldName);
            if (targetField == null || !ClassUtils.isAssignable(sourceField.getType(), targetField.getType())) continue;
            Object sourceVal = ReflectFieldUtils.getValue(sourceField, source);
            ReflectFieldUtils.setValue(targetField, target, sourceVal);
        }
    }

    public static boolean isSameType(Object one, Object two) {
        if (ObjectUtils.isNull(one) || ObjectUtils.isNull(two)) {
            return false;
        }
        Class<?> clazzOne = one.getClass();
        return clazzOne.isInstance(two);
    }

    public static boolean isNotSameType(Object one, Object two) {
        return !ObjectUtils.isSameType(one, two);
    }

    public static boolean isNull(Object object) {
        return null == object;
    }

    public static boolean isNotNull(Object object) {
        return !ObjectUtils.isNull(object);
    }

    public static boolean isEmpty(Object object) {
        if (ObjectUtils.isNull(object)) {
            return true;
        }
        if (object instanceof String) {
            String string = (String)object;
            return StringUtils.isEmpty(string);
        }
        if (object instanceof Collection) {
            Collection collection = (Collection)object;
            return CollectionUtils.isEmpty(collection);
        }
        if (object instanceof Map) {
            Map map = (Map)object;
            return MapUtils.isEmpty(map);
        }
        if (object.getClass().isArray()) {
            return Array.getLength(object) == 0;
        }
        return false;
    }

    public static boolean isNotEmpty(Object object) {
        return !ObjectUtils.isEmpty(object);
    }

    public static boolean isEquals(Object except, Object real) {
        if (ObjectUtils.isNotSameType(except, real)) {
            return false;
        }
        Class<?> exceptClass = except.getClass();
        Class<?> realClass = except.getClass();
        if (exceptClass.isPrimitive() && realClass.isPrimitive() && except != real) {
            return false;
        }
        if (ClassTypeUtils.isArray(exceptClass) && ClassTypeUtils.isArray(realClass)) {
            Object[] exceptArray = (Object[])except;
            Object[] realArray = (Object[])real;
            return Arrays.equals(exceptArray, realArray);
        }
        if (ClassTypeUtils.isMap(exceptClass) && ClassTypeUtils.isMap(realClass)) {
            Map exceptMap = (Map)except;
            Map realMap = (Map)real;
            return exceptMap.equals(realMap);
        }
        return except.equals(real);
    }

    public static boolean isNotEquals(Object except, Object real) {
        return !ObjectUtils.isEquals(except, real);
    }

    public static String objectToString(Object object) {
        return ObjectUtils.objectToString(object, null);
    }

    public static String objectToString(Object object, String defaultValue) {
        if (ObjectUtils.isNull(object)) {
            return defaultValue;
        }
        return object.toString();
    }

    public static boolean isNull(Object object, Object ... others) {
        if (ObjectUtils.isNull(object)) {
            if (ArrayUtils.isNotEmpty(others)) {
                for (Object other : others) {
                    if (!ObjectUtils.isNotNull(other)) continue;
                    return false;
                }
                return true;
            }
            return true;
        }
        return false;
    }

    public static boolean isEqualsOrNull(Object left, Object right) {
        if (ObjectUtils.isNull(left, right)) {
            return true;
        }
        if (ObjectUtils.isNull(left) || ObjectUtils.isNull(right)) {
            return false;
        }
        return ObjectUtils.isEquals(left, right);
    }

    public static boolean isSameValue(Object valueOne, Object valueTwo) {
        if (valueOne == null && valueTwo == null) {
            return true;
        }
        if (valueOne == null || valueTwo == null) {
            return false;
        }
        return valueOne.equals(valueTwo);
    }

    public static <T> void ifEmpty(T obj, Consumer<T> consumer) {
        if (ObjectUtil.isEmpty(obj)) {
            consumer.accept(obj);
        }
    }

    public static <T> void ifNotEmpty(T obj, Consumer<T> consumer) {
        if (ObjectUtil.isNotEmpty(obj)) {
            consumer.accept(obj);
        }
    }

    public static <T> void ifNull(T obj, Consumer<T> consumer) {
        if (ObjectUtil.isNull(obj)) {
            consumer.accept(obj);
        }
    }

    public static <T> void ifNotNull(T obj, Consumer<T> consumer) {
        if (ObjectUtil.isNotNull(obj)) {
            consumer.accept(obj);
        }
    }

    public static <T> T requireNotNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }
}

