/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.reflect;

import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ClassTypeUtils {
    private static final Class[] BASE_TYPE_CLASS = new Class[]{String.class, Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Void.class, Object.class, Class.class};

    private ClassTypeUtils() {
    }

    public static boolean isMap(Class<?> clazz) {
        return Map.class.isAssignableFrom(clazz);
    }

    public static boolean isArray(Class<?> clazz) {
        return clazz.isArray();
    }

    public static boolean isCollection(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz);
    }

    public static boolean isIterable(Class<?> clazz) {
        return Iterable.class.isAssignableFrom(clazz);
    }

    public static boolean isBase(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return true;
        }
        for (Class baseClazz : BASE_TYPE_CLASS) {
            if (!baseClazz.equals(clazz)) continue;
            return true;
        }
        return false;
    }

    public static boolean isEnum(Class<?> clazz) {
        ArgUtils.notNull(clazz, "clazz");
        return clazz.isEnum();
    }

    public static boolean isAbstract(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    public static boolean isAbstractOrInterface(Class<?> clazz) {
        return ClassTypeUtils.isAbstract(clazz) || clazz.isInterface();
    }

    public static boolean isJavaBean(Class<?> clazz) {
        return null != clazz && !clazz.isInterface() && !ClassTypeUtils.isAbstract(clazz) && !clazz.isEnum() && !clazz.isArray() && !clazz.isAnnotation() && !clazz.isSynthetic() && !clazz.isPrimitive() && !ClassTypeUtils.isIterable(clazz) && !ClassTypeUtils.isMap(clazz);
    }

    public static boolean isJdk(Class<?> clazz) {
        return clazz != null && clazz.getClassLoader() == null;
    }

    public static boolean isBean(Class<?> clazz) {
        if (ClassTypeUtils.isJavaBean(clazz)) {
            Method[] methods;
            for (Method method : methods = clazz.getMethods()) {
                if (method.getParameterTypes().length != 1 || !method.getName().startsWith("set")) continue;
                return true;
            }
        }
        return false;
    }

    public static Class getListType(Field field) {
        ParameterizedType listGenericType = (ParameterizedType)field.getGenericType();
        Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
        return (Class)listActualTypeArguments[0];
    }

    public static boolean isWildcardGenericType(Type type) {
        return false;
    }

    public static boolean isList(Class clazz) {
        return List.class.isAssignableFrom(clazz);
    }

    public static boolean isSet(Class clazz) {
        return Set.class.isAssignableFrom(clazz);
    }

    public static boolean isPrimitive(Class clazz) {
        return clazz.isPrimitive();
    }

    public static boolean isPrimitive(Object object) {
        if (ObjectUtils.isNull(object)) {
            return false;
        }
        return ClassTypeUtils.isPrimitive(object.getClass());
    }
}

