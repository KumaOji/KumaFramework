/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonCreator
 *  com.fasterxml.jackson.annotation.JsonCreator$Mode
 *  org.jspecify.annotations.Nullable
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.core.convert.converter.ConditionalGenericConverter
 *  org.springframework.core.convert.converter.GenericConverter$ConvertiblePair
 */
package com.kuma.boot.common.utils.convert;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.convert.ConvertUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
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

public class StringToEnumConverter
implements ConditionalGenericConverter {
    private static final ConcurrentMap<Class<?>, AccessibleObject> ENUM_CACHE_MAP = new ConcurrentHashMap(8);

    private static @Nullable AccessibleObject getAnnotation(Class<?> clazz) {
        HashSet accessibleObjects = new HashSet();
        Constructor<?>[] constructors = clazz.getConstructors();
        Collections.addAll(accessibleObjects, constructors);
        Method[] methods = clazz.getDeclaredMethods();
        Collections.addAll(accessibleObjects, methods);
        for (AccessibleObject accessibleObject : accessibleObjects) {
            JsonCreator jsonCreator = accessibleObject.getAnnotation(JsonCreator.class);
            if (jsonCreator == null || JsonCreator.Mode.DISABLED == jsonCreator.mode()) continue;
            accessibleObject.setAccessible(true);
            return accessibleObject;
        }
        return null;
    }

    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return true;
    }

    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(String.class, Enum.class));
    }

    public @Nullable Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (StringUtils.isBlank((String)source)) {
            return null;
        }
        Class clazz = targetType.getType();
        AccessibleObject accessibleObject = CollectionUtils.computeIfAbsent(ENUM_CACHE_MAP, clazz, StringToEnumConverter::getAnnotation);
        String value = ((String)source).trim();
        if (accessibleObject == null) {
            return StringToEnumConverter.valueOf(clazz, value);
        }
        try {
            return StringToEnumConverter.invoke(clazz, accessibleObject, value);
        }
        catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
            return null;
        }
    }

    private static <T extends Enum<T>> T valueOf(Class<?> clazz, String value) {
        return (T)Enum.valueOf(clazz, value);
    }

    private static @Nullable Object invoke(Class<?> clazz, AccessibleObject accessibleObject, String value) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (accessibleObject instanceof Constructor) {
            Constructor constructor = (Constructor)accessibleObject;
            Class<?> paramType = constructor.getParameterTypes()[0];
            Object object = ConvertUtils.convert((Object)value, paramType);
            return constructor.newInstance(object);
        }
        if (accessibleObject instanceof Method) {
            Method method = (Method)accessibleObject;
            Class<?> paramType = method.getParameterTypes()[0];
            Object object = ConvertUtils.convert((Object)value, paramType);
            return method.invoke(clazz, object);
        }
        return null;
    }
}

