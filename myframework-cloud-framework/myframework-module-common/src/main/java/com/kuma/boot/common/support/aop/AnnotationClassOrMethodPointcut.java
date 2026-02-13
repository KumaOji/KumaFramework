/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.NonNull
 *  org.springframework.aop.ClassFilter
 *  org.springframework.aop.MethodMatcher
 *  org.springframework.aop.support.StaticMethodMatcherPointcut
 *  org.springframework.aop.support.annotation.AnnotationClassFilter
 *  org.springframework.aop.support.annotation.AnnotationMethodMatcher
 *  org.springframework.core.annotation.AnnotationUtils
 */
package com.kuma.boot.common.support.aop;

import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.reflect.ReflectionUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jspecify.annotations.NonNull;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.aop.support.annotation.AnnotationClassFilter;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;
import org.springframework.core.annotation.AnnotationUtils;

final class AnnotationClassOrMethodPointcut
extends StaticMethodMatcherPointcut {
    private final MethodMatcher methodResolver;

    public AnnotationClassOrMethodPointcut(Class<? extends Annotation> annotationType) {
        this.methodResolver = new AnnotationMethodMatcher(annotationType);
        this.setClassFilter((ClassFilter)new AnnotationClassOrMethodFilter(annotationType));
    }

    public boolean matches(@NonNull Method method, @NonNull Class<?> targetClass) {
        return this.getClassFilter().matches(targetClass) || this.methodResolver.matches(method, targetClass);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AnnotationClassOrMethodPointcut)) {
            return false;
        }
        AnnotationClassOrMethodPointcut otherAdvisor = (AnnotationClassOrMethodPointcut)((Object)other);
        return ObjectUtils.nullSafeEquals((Object)this.methodResolver, (Object)otherAdvisor.methodResolver);
    }

    private static final class AnnotationClassOrMethodFilter
    extends AnnotationClassFilter {
        private final AnnotationMethodsResolver methodResolver;

        AnnotationClassOrMethodFilter(Class<? extends Annotation> annotationType) {
            super(annotationType, true);
            this.methodResolver = new AnnotationMethodsResolver(annotationType);
        }

        public boolean matches(@NonNull Class<?> clazz) {
            return super.matches(clazz) || this.methodResolver.hasAnnotatedMethods(clazz);
        }
    }

    private static class AnnotationMethodsResolver {
        private final Class<? extends Annotation> annotationType;

        public AnnotationMethodsResolver(Class<? extends Annotation> annotationType) {
            this.annotationType = annotationType;
        }

        public boolean hasAnnotatedMethods(Class<?> clazz) {
            AtomicBoolean found = new AtomicBoolean(false);
            ReflectionUtils.doWithMethods(clazz, method -> {
                if (found.get()) {
                    return;
                }
                Annotation annotation = AnnotationUtils.findAnnotation((Method)method, this.annotationType);
                if (annotation != null) {
                    found.set(true);
                }
            });
            return found.get();
        }
    }
}

