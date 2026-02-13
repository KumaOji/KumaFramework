/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 */
package com.kuma.boot.common.utils.reflect;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ReflectAnnotationUtils {
    private ReflectAnnotationUtils() {
    }

    public static void updateValue(Annotation annotation, String method, Object value) {
        Map<String, Object> memberValues = ReflectAnnotationUtils.getAnnotationAttributes(annotation);
        memberValues.put(method, value);
    }

    public static Object getValue(Annotation annotation, String method) {
        Map<String, Object> memberValues = ReflectAnnotationUtils.getAnnotationAttributes(annotation);
        return memberValues.get(method);
    }

    public static String getValueStr(Annotation annotation, String method) {
        Map<String, Object> memberValues = ReflectAnnotationUtils.getAnnotationAttributes(annotation);
        Object object = memberValues.get(method);
        return ObjectUtils.objectToString(object);
    }

    public static Map<String, Object> getAnnotationAttributes(Annotation annotation) {
        try {
            InvocationHandler h = Proxy.getInvocationHandler(annotation);
            Field hField = h.getClass().getDeclaredField("memberValues");
            hField.setAccessible(true);
            return (Map)hField.get(h);
        }
        catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<Annotation> getAnnotation(Annotation annotation, Class<? extends Annotation> annotationClass) {
        if (ObjectUtils.isNull(annotation) || ObjectUtils.isNull(annotationClass)) {
            return Optional.empty();
        }
        Annotation atAnnotation = annotation.annotationType().getAnnotation(annotationClass);
        if (ObjectUtils.isNotNull(atAnnotation)) {
            return Optional.of(atAnnotation);
        }
        return Optional.empty();
    }

    public static Optional<Annotation> getAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        ArgUtils.notNull(clazz, "clazz");
        ArgUtils.notNull(annotationClass, "annotationClass");
        if (clazz.isAnnotationPresent(annotationClass)) {
            Annotation annotation = clazz.getAnnotation(annotationClass);
            return Optional.of(annotation);
        }
        return Optional.empty();
    }

    public static List<Annotation> getAnnotationRefs(Class clazz, Class<? extends Annotation> annotationClass) {
        ArgUtils.notNull(clazz, "clazz");
        ArgUtils.notNull(annotationClass, "annotationClass");
        HashSet annotationSet = Sets.newHashSet();
        Object[] annotations = clazz.getAnnotations();
        if (ArrayUtils.isEmpty(annotations)) {
            return Lists.newArrayList();
        }
        for (Object annotation : annotations) {
            if (annotation.annotationType().equals(annotationClass)) {
                annotationSet.add(annotation);
                continue;
            }
            if (!annotation.annotationType().isAnnotationPresent(annotationClass)) continue;
            annotationSet.add(annotation);
        }
        return Lists.newArrayList((Iterable)annotationSet);
    }
}

