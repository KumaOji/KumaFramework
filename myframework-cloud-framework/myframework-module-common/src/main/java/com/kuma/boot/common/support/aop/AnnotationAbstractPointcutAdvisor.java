/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.aop.Advice
 *  org.aopalliance.intercept.MethodInvocation
 *  org.jspecify.annotations.NonNull
 *  org.springframework.aop.IntroductionInterceptor
 *  org.springframework.aop.support.AbstractPointcutAdvisor
 *  org.springframework.core.GenericTypeResolver
 *  org.springframework.util.Assert
 */
package com.kuma.boot.common.support.aop;

import com.kuma.boot.common.support.aop.AnnotationTarget;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInvocation;
import org.jspecify.annotations.NonNull;
import org.springframework.aop.IntroductionInterceptor;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.Assert;

public abstract class AnnotationAbstractPointcutAdvisor<A extends Annotation>
extends AbstractPointcutAdvisor
implements IntroductionInterceptor {
    protected final Class<A> annotationType = GenericTypeResolver.resolveTypeArgument(((Object)((Object)this)).getClass(), AnnotationAbstractPointcutAdvisor.class);

    public @NonNull Object invoke(@NonNull MethodInvocation invocation) throws Throwable {
        Assert.notNull(this.annotationType, (String)"annotationType must not be null");
        Method method = invocation.getMethod();
        AnnotationTarget<A> target = AnnotationTarget.of(this.annotationType);
        A annotation = target.getAnnotation(method);
        if (annotation == null && invocation.getThis() != null) {
            annotation = target.getAnnotation(invocation.getThis().getClass());
        }
        return this.invoke(invocation, annotation);
    }

    public int getOrder() {
        int order = super.getOrder();
        return order == Integer.MAX_VALUE ? 0x7FFFFFFE : order;
    }

    protected abstract Object invoke(MethodInvocation var1, A var2) throws Throwable;

    public boolean implementsInterface(@NonNull Class<?> intf) {
        return intf.isAssignableFrom(((Object)((Object)this)).getClass());
    }

    public @NonNull Advice getAdvice() {
        return this;
    }
}

