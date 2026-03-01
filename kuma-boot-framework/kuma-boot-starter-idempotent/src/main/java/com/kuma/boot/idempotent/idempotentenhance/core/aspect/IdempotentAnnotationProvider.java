/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.aspectj.lang.JoinPoint
 *  org.aspectj.lang.ProceedingJoinPoint
 *  org.aspectj.lang.reflect.MethodSignature
 *  org.springframework.core.DefaultParameterNameDiscoverer
 *  org.springframework.core.ParameterNameDiscoverer
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.Expression
 *  org.springframework.expression.ExpressionParser
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 */
package com.kuma.boot.idempotent.idempotentenhance.core.aspect;

import com.kuma.boot.common.utils.log.LogUtils;
import java.lang.reflect.Method;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class IdempotentAnnotationProvider {
    private static final ParameterNameDiscoverer NAME_DISCOVERER = new DefaultParameterNameDiscoverer();
    private static final ExpressionParser PARSER = new SpelExpressionParser();

    public static String[] getParameterNames(ProceedingJoinPoint joinPoint) {
        Method method = IdempotentAnnotationProvider.getMethod((JoinPoint)joinPoint);
        return NAME_DISCOVERER.getParameterNames(method);
    }

    public static String[] getParameterNames(Method method) {
        return NAME_DISCOVERER.getParameterNames(method);
    }

    public static String parse(String expressionStr, EvaluationContext context) {
        Expression expression = PARSER.parseExpression(expressionStr);
        return (String)expression.getValue(context, String.class);
    }

    public static Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        if (method.getDeclaringClass().isInterface()) {
            try {
                method = joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(), method.getParameterTypes());
            }
            catch (Exception e) {
                LogUtils.error((Throwable)e);
            }
        }
        return method;
    }
}

