/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.context.expression.AnnotatedElementKey
 *  org.springframework.context.expression.BeanFactoryResolver
 *  org.springframework.context.expression.CachedExpressionEvaluator
 *  org.springframework.context.expression.CachedExpressionEvaluator$ExpressionKey
 *  org.springframework.context.expression.MethodBasedEvaluationContext
 *  org.springframework.expression.BeanResolver
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.Expression
 */
package com.kuma.boot.common.utils.spel;

import com.kuma.boot.common.utils.spel.ExpressionRootObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jspecify.annotations.Nullable;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

public class ExpressionEvaluator
extends CachedExpressionEvaluator {
    private final Map<CachedExpressionEvaluator.ExpressionKey, Expression> expressionCache = new ConcurrentHashMap<CachedExpressionEvaluator.ExpressionKey, Expression>(64);
    private final Map<AnnotatedElementKey, Method> methodCache = new ConcurrentHashMap<AnnotatedElementKey, Method>(64);

    public EvaluationContext createContext(Method method, Object[] args, Object target, Class<?> targetClass, @Nullable BeanFactory beanFactory) {
        Method targetMethod = this.getTargetMethod(targetClass, method);
        ExpressionRootObject rootObject = new ExpressionRootObject(method, args, target, targetClass, targetMethod);
        MethodBasedEvaluationContext evaluationContext = new MethodBasedEvaluationContext((Object)rootObject, targetMethod, args, this.getParameterNameDiscoverer());
        if (beanFactory != null) {
            evaluationContext.setBeanResolver((BeanResolver)new BeanFactoryResolver(beanFactory));
        }
        return evaluationContext;
    }

    public EvaluationContext createContext(Method method, Object[] args, Class<?> targetClass, Object rootObject, @Nullable BeanFactory beanFactory) {
        Method targetMethod = this.getTargetMethod(targetClass, method);
        MethodBasedEvaluationContext evaluationContext = new MethodBasedEvaluationContext(rootObject, targetMethod, args, this.getParameterNameDiscoverer());
        if (beanFactory != null) {
            evaluationContext.setBeanResolver((BeanResolver)new BeanFactoryResolver(beanFactory));
        }
        return evaluationContext;
    }

    public @Nullable Object eval(String expression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
        return this.eval(expression, methodKey, evalContext, null);
    }

    public <T> @Nullable T eval(String expression, AnnotatedElementKey methodKey, EvaluationContext evalContext, @Nullable Class<T> valueType) {
        return (T)this.getExpression(this.expressionCache, methodKey, expression).getValue(evalContext, valueType);
    }

    public @Nullable String evalAsText(String expression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
        return this.eval(expression, methodKey, evalContext, String.class);
    }

    public boolean evalAsBool(String expression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
        return Boolean.TRUE.equals(this.eval(expression, methodKey, evalContext, Boolean.class));
    }

    private Method getTargetMethod(Class<?> targetClass, Method method) {
        AnnotatedElementKey methodKey = new AnnotatedElementKey((AnnotatedElement)method, targetClass);
        return this.methodCache.computeIfAbsent(methodKey, key -> AopUtils.getMostSpecificMethod((Method)method, (Class)targetClass));
    }

    public void clear() {
        this.expressionCache.clear();
        this.methodCache.clear();
    }
}

