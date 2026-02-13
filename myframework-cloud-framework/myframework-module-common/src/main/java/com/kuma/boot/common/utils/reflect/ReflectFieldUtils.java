/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.reflect;

import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.reflect.ClassTypeUtils;
import com.kuma.boot.common.utils.reflect.ClassUtils;
import com.kuma.boot.common.utils.reflect.TypeUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public final class ReflectFieldUtils {
    private ReflectFieldUtils() {
    }

    public static boolean isAssignable(Field sourceField, Field targetField) {
        if (ObjectUtils.isNull(sourceField) || ObjectUtils.isNull(targetField)) {
            return false;
        }
        if (Modifier.isFinal(targetField.getModifiers())) {
            return false;
        }
        Class<?> sourceType = sourceField.getType();
        Class<?> targetType = targetField.getType();
        return ClassUtils.isAssignable(sourceType, targetType);
    }

    public static Boolean isString(Field field) {
        return field.getType() == String.class;
    }

    public static Boolean isNotString(Field field) {
        return ReflectFieldUtils.isString(field) == false;
    }

    public static boolean isAnnotationPresent(Field field, Class<? extends Annotation> clazz) {
        return field.isAnnotationPresent(clazz);
    }

    public static boolean isNotAnnotationPresent(Field field, Class<? extends Annotation> clazz) {
        return !ReflectFieldUtils.isAnnotationPresent(field, clazz);
    }

    public static Class getGenericParamType(Field field, int paramIndex) {
        if (ObjectUtils.isNull(field)) {
            return null;
        }
        field.setAccessible(true);
        Type genericType = field.getGenericType();
        return TypeUtils.getGenericParamType(genericType, paramIndex);
    }

    public static boolean containsAnnotationField(Class clazz, Class<? extends Annotation> annotationClass) {
        ArgUtils.notNull(clazz, "Clazz");
        ArgUtils.notNull(annotationClass, "Annotation class");
        List<Field> fieldList = ClassUtils.getAllFieldList(clazz);
        if (CollectionUtils.isEmpty(fieldList)) {
            return false;
        }
        for (Field field : fieldList) {
            if (!field.isAnnotationPresent(annotationClass)) continue;
            return true;
        }
        return false;
    }

    public static Class getComponentType(Field field, int typeIndex) {
        Class<?> fieldType = field.getType();
        if (ClassTypeUtils.isArray(fieldType)) {
            return fieldType.getComponentType();
        }
        if (ClassTypeUtils.isCollection(fieldType)) {
            return ReflectFieldUtils.getGenericParamType(field, 0);
        }
        if (ClassTypeUtils.isMap(fieldType)) {
            return ReflectFieldUtils.getGenericParamType(field, typeIndex);
        }
        return fieldType;
    }

    public static Class getComponentType(Field field) {
        return ReflectFieldUtils.getComponentType(field, 0);
    }

    public static void setValue(Field field, Object instance, Object fieldValue) {
        try {
            field.setAccessible(true);
            field.set(instance, fieldValue);
        }
        catch (IllegalAccessException e) {
            throw new BootException(e);
        }
    }

    public static void setValue(Object instance, String fieldName, Object fieldValue) {
        ArgUtils.notNull(instance, "instance");
        try {
            Class<?> clazz = instance.getClass();
            Map<String, Field> fieldNameMap = ClassUtils.getAllFieldMap(clazz);
            Field field = fieldNameMap.get(fieldName);
            field.setAccessible(true);
            field.set(instance, fieldValue);
        }
        catch (IllegalAccessException e) {
            throw new BootException(e);
        }
    }

    public static Object getValue(Field field, Object instance) {
        try {
            field.setAccessible(true);
            return field.get(instance);
        }
        catch (IllegalAccessException e) {
            throw new BootException(e);
        }
    }

    public static Object getValue(String fieldName, Object instance) {
        Field field = ReflectFieldUtils.getField(instance, fieldName);
        return ReflectFieldUtils.getValue(field, instance);
    }

    public static Field getField(Object object, String fieldName) {
        ArgUtils.notNull(object, "object");
        Class<?> clazz = object.getClass();
        return ReflectFieldUtils.getField(clazz, fieldName);
    }

    public static Field getField(Class clazz, String fieldName) {
        ArgUtils.notNull(clazz, "clazz");
        ArgUtils.notEmpty(fieldName, "fieldName");
        List<Field> fieldList = ClassUtils.getAllFieldList(clazz);
        for (Field field : fieldList) {
            String name = field.getName();
            if (!name.equals(fieldName)) continue;
            return field;
        }
        throw new BootException("Field not found for fieldName: " + fieldName);
    }
}

