/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  org.springframework.aop.Pointcut
 *  org.springframework.aop.support.annotation.AnnotationMatchingPointcut
 */
package com.kuma.boot.common.support.aop;

import com.google.common.collect.Sets;
import com.kuma.boot.common.support.aop.AnnotationAutoPointcut;
import com.kuma.boot.common.support.aop.AnnotationClassOrMethodPointcut;
import com.kuma.boot.common.utils.reflect.AnnotationUtils;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;

final class AnnotationTarget<A extends Annotation> {
    public static final AnnotationAutoPointcut POINTCUT = new AnnotationTargetPointcut();
    private static final ConcurrentMap<Class<? extends Annotation>, AnnotationTarget<?>> CACHE = new ConcurrentHashMap();
    private final Class<A> annotationType;
    private final Set<ElementType> elementTypes;

    public static <A extends Annotation> AnnotationTarget<A> of(Class<A> annotationType) {
        return (AnnotationTarget<A>) CACHE.computeIfAbsent(annotationType, AnnotationTarget::new);
    }

    private AnnotationTarget(Class<A> annotationType) {
        this.annotationType = annotationType;
        Target target = annotationType.getAnnotation(Target.class);
        this.elementTypes = Sets.newHashSet();
    }

    public A getAnnotation(Method method) {
        return (A)(this.supportMethod() ? AnnotationUtils.getAnnotation((Method)method, this.annotationType) : null);
    }

    public A getAnnotation(Class<?> clazz) {
        return (A)(this.supportType() ? AnnotationUtils.getAnnotation(clazz, this.annotationType) : null);
    }

    private boolean supportMethod() {
        return this.elementTypes.contains((Object)ElementType.METHOD);
    }

    private boolean supportType() {
        return this.elementTypes.contains((Object)ElementType.TYPE);
    }

    static final class AnnotationTargetPointcut
    implements AnnotationAutoPointcut {
        AnnotationTargetPointcut() {
        }

        @Override
        public Pointcut getPointcut(Class<? extends Annotation> annotationType) {
            AnnotationTarget<? extends Annotation> target = AnnotationTarget.of(annotationType);
            if (target.supportType() && target.supportMethod()) {
                return new AnnotationClassOrMethodPointcut(annotationType);
            }
            if (target.supportType()) {
                return AnnotationMatchingPointcut.forClassAnnotation(annotationType);
            }
            if (target.supportMethod()) {
                return AnnotationMatchingPointcut.forMethodAnnotation(annotationType);
            }
            throw new IllegalArgumentException("annotation:" + String.valueOf(annotationType) + " Missing " + String.valueOf(Target.class) + " TYPE or METHOD information");
        }
    }
}

