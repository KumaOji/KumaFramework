/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.web.request.altas.util;

import com.kuma.boot.common.utils.log.LogUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionUtils {
    private static final ConcurrentHashMap<Method, Parameter[]> PARAMETER_CACHE = new ConcurrentHashMap();
    private static final ConcurrentHashMap<Class<?>, Field[]> FIELD_CACHE = new ConcurrentHashMap();

    public static Parameter[] getParameters(Method method) {
        return PARAMETER_CACHE.computeIfAbsent(method, Executable::getParameters);
    }

    public static Field[] getAllFields(Class<?> clazz) {
        return FIELD_CACHE.computeIfAbsent(clazz, ReflectionUtils::doGetAllFields);
    }

    private static Field[] doGetAllFields(Class<?> clazz) {
        ArrayList<Field> fields = new ArrayList<Field>();
        for (Class<?> currentClass = clazz; currentClass != null; currentClass = currentClass.getSuperclass()) {
            Field[] declaredFields;
            for (Field field : declaredFields = currentClass.getDeclaredFields()) {
                fields.add(field);
            }
        }
        return fields.toArray(new Field[0]);
    }

    public static <T extends Annotation> T getAnnotation(Parameter parameter, Class<T> annotationClass) {
        return parameter.getAnnotation(annotationClass);
    }

    public static <T extends Annotation> T getAnnotation(Method method, Class<T> annotationClass) {
        return method.getAnnotation(annotationClass);
    }

    public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationClass) {
        return clazz.getAnnotation(annotationClass);
    }

    public static Object getFieldValue(Field field, Object obj) {
        try {
            field.setAccessible(true);
            return field.get(obj);
        }
        catch (Exception e) {
            LogUtils.debug((String)"Failed to get field value: {}.{}", (Object[])new Object[]{obj.getClass().getSimpleName(), field.getName(), e});
            return null;
        }
    }

    public static boolean setFieldValue(Field field, Object obj, Object value) {
        try {
            field.setAccessible(true);
            field.set(obj, value);
            return true;
        }
        catch (Exception e) {
            LogUtils.debug((String)"Failed to set field value: {}.{}", (Object[])new Object[]{obj.getClass().getSimpleName(), field.getName(), e});
            return false;
        }
    }

    public static String formatMethodSignature(Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getDeclaringClass().getSimpleName()).append(".").append(method.getName()).append("(");
        Parameter[] parameters = ReflectionUtils.getParameters(method);
        for (int i = 0; i < parameters.length; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(parameters[i].getType().getSimpleName());
        }
        sb.append(")");
        return sb.toString();
    }

    public static void clearCaches() {
        PARAMETER_CACHE.clear();
        FIELD_CACHE.clear();
    }

    public static String getCacheStats() {
        return String.format("ParameterCache: %d, FieldCache: %d", PARAMETER_CACHE.size(), FIELD_CACHE.size());
    }
}

