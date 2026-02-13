/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.NonNull
 *  org.springframework.aop.Pointcut
 */
package com.kuma.boot.common.support.aop;

import com.kuma.boot.common.support.aop.AnnotationAbstractPointcutAdvisor;
import com.kuma.boot.common.support.aop.AnnotationAutoPointcut;
import java.lang.annotation.Annotation;
import org.jspecify.annotations.NonNull;
import org.springframework.aop.Pointcut;

public abstract class AnnotationAbstractPointcutTypeAdvisor<A extends Annotation>
extends AnnotationAbstractPointcutAdvisor<A> {
    public @NonNull Pointcut getPointcut() {
        return this.autoPointcut().getPointcut(this.annotationType);
    }

    protected AnnotationAutoPointcut autoPointcut() {
        return AnnotationAutoPointcut.auto();
    }
}

