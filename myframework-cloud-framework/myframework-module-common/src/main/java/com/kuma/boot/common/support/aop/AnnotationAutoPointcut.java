/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.Pointcut
 *  org.springframework.aop.support.annotation.AnnotationMatchingPointcut
 */
package com.kuma.boot.common.support.aop;

import com.kuma.boot.common.support.aop.AnnotationClassOrMethodPointcut;
import com.kuma.boot.common.support.aop.AnnotationTarget;
import java.lang.annotation.Annotation;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;

@FunctionalInterface
public interface AnnotationAutoPointcut {
    public Pointcut getPointcut(Class<? extends Annotation> var1);

    public static AnnotationAutoPointcut type() {
        return AnnotationMatchingPointcut::forClassAnnotation;
    }

    public static AnnotationAutoPointcut method() {
        return AnnotationMatchingPointcut::forMethodAnnotation;
    }

    public static AnnotationAutoPointcut typeOrMethod() {
        return AnnotationClassOrMethodPointcut::new;
    }

    public static AnnotationAutoPointcut auto() {
        return AnnotationTarget.POINTCUT;
    }
}

