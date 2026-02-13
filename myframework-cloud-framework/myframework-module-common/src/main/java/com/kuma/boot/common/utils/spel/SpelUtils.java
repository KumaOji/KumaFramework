/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aspectj.lang.JoinPoint
 *  org.aspectj.lang.reflect.MethodSignature
 *  org.springframework.core.DefaultParameterNameDiscoverer
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 *  org.springframework.expression.spel.support.StandardEvaluationContext
 */
package com.kuma.boot.common.utils.spel;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpelUtils {
    private static final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
    private static final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public static String compileParams(JoinPoint joinPoint, String spel) {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(signature.getMethod());
        if (parameterNames != null && parameterNames.length > 0) {
            StandardEvaluationContext context = new StandardEvaluationContext();
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < args.length; ++i) {
                context.setVariable(parameterNames[i], args[i]);
            }
            return spelExpressionParser.parseExpression(spel).getValue((EvaluationContext)context).toString();
        }
        return "";
    }

    public static String compileParams(JoinPoint joinPoint, Object rvt, String spel) {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(signature.getMethod());
        if (parameterNames != null && parameterNames.length > 0) {
            StandardEvaluationContext context = new StandardEvaluationContext();
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < args.length; ++i) {
                context.setVariable(parameterNames[i], args[i]);
            }
            context.setVariable("rvt", rvt);
            return spelExpressionParser.parseExpression(spel).getValue((EvaluationContext)context).toString();
        }
        return "";
    }
}

