/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.core.annotation.AnnotationAttributes
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.core.type.AnnotatedTypeMetadata
 */
package com.kuma.boot.common.utils.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class AnnotationUtils
extends org.springframework.core.annotation.AnnotationUtils {
    public static <A extends Annotation> A getAnnotationElement(MethodParameter methodParameter, Class<A> annotationClass) {
        Annotation annotation = methodParameter.getMethodAnnotation(annotationClass);
        if (annotation == null) {
            Class containingClass = methodParameter.getContainingClass();
            annotation = AnnotatedElementUtils.getMergedAnnotation((AnnotatedElement)containingClass, annotationClass);
        }
        return (A)annotation;
    }

    public static <A extends Annotation> A getAnnotationElement(Method method, Class<A> annotationClass) {
        Annotation annotation = AnnotationUtils.getAnnotation((Method)method, annotationClass);
        if (annotation == null) {
            annotation = AnnotationUtils.getAnnotation(method.getDeclaringClass(), annotationClass);
        }
        return (A)annotation;
    }

    public static <A extends Annotation> boolean hasAnnotationElement(MethodParameter methodParameter, Class<A> annotationClass) {
        Class containingClass = methodParameter.getContainingClass();
        return AnnotatedElementUtils.hasAnnotation((AnnotatedElement)containingClass, annotationClass) || methodParameter.hasMethodAnnotation(annotationClass);
    }

    public static <A extends Annotation> boolean hasAnnotationElement(Method method, Class<A> annotationClass) {
        return method.isAnnotationPresent(annotationClass) || AnnotatedElementUtils.hasAnnotation(method.getDeclaringClass(), annotationClass);
    }

    public static AnnotationAttributes attributesFor(AnnotatedTypeMetadata metadata, String annotationClassName) {
        return AnnotationAttributes.fromMap((Map)metadata.getAnnotationAttributes(annotationClassName));
    }

    public static <A extends Annotation> AnnotationAttributes attributesFor(AnnotatedTypeMetadata metadata, Class<A> annotationClass) {
        return AnnotationUtils.attributesFor(metadata, annotationClass.getName());
    }
}

