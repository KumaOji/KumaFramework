/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonValue
 *  org.jspecify.annotations.Nullable
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.core.convert.converter.ConditionalGenericConverter
 *  org.springframework.core.convert.converter.GenericConverter$ConvertiblePair
 */
package com.kuma.boot.common.utils.convert;

import com.fasterxml.jackson.annotation.JsonValue;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.convert.ConvertUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.jspecify.annotations.Nullable;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;

public class EnumToStringConverter
implements ConditionalGenericConverter {
    private static final ConcurrentMap<Class<?>, AccessibleObject> ENUM_CACHE_MAP = new ConcurrentHashMap(8);

    private static @Nullable AccessibleObject getAnnotation(Class<?> clazz) {
        HashSet accessibleObjects = new HashSet();
        Field[] fields = clazz.getDeclaredFields();
        Collections.addAll(accessibleObjects, fields);
        Method[] methods = clazz.getDeclaredMethods();
        Collections.addAll(accessibleObjects, methods);
        for (AccessibleObject accessibleObject : accessibleObjects) {
            JsonValue jsonValue = accessibleObject.getAnnotation(JsonValue.class);
            if (jsonValue == null || !jsonValue.value()) continue;
            accessibleObject.setAccessible(true);
            return accessibleObject;
        }
        return null;
    }

    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return true;
    }

    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        HashSet<GenericConverter.ConvertiblePair> pairSet = new HashSet<GenericConverter.ConvertiblePair>(3);
        pairSet.add(new GenericConverter.ConvertiblePair(Enum.class, String.class));
        pairSet.add(new GenericConverter.ConvertiblePair(Enum.class, Integer.class));
        pairSet.add(new GenericConverter.ConvertiblePair(Enum.class, Long.class));
        return Collections.unmodifiableSet(pairSet);
    }

    public @Nullable Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        Class sourceClazz = sourceType.getType();
        AccessibleObject accessibleObject = CollectionUtils.computeIfAbsent(ENUM_CACHE_MAP, sourceClazz, EnumToStringConverter::getAnnotation);
        Class targetClazz = targetType.getType();
        if (accessibleObject == null) {
            if (String.class == targetClazz) {
                return ((Enum)source).name();
            }
            int ordinal = ((Enum)source).ordinal();
            return ConvertUtils.convert((Object)ordinal, targetClazz);
        }
        try {
            return EnumToStringConverter.invoke(sourceClazz, accessibleObject, source, targetClazz);
        }
        catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
            return null;
        }
    }

    private static @Nullable Object invoke(Class<?> clazz, AccessibleObject accessibleObject, Object source, Class<?> targetClazz) throws IllegalAccessException, InvocationTargetException {
        Object value = null;
        if (accessibleObject instanceof Field) {
            Field field = (Field)accessibleObject;
            value = field.get(source);
        } else if (accessibleObject instanceof Method) {
            Method method = (Method)accessibleObject;
            Class<?> paramType = method.getParameterTypes()[0];
            Object object = ConvertUtils.convert(source, paramType);
            value = method.invoke(clazz, object);
        }
        if (value == null) {
            return null;
        }
        return ConvertUtils.convert(value, targetClazz);
    }
}

