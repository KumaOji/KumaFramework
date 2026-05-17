/*
 *  com.kuma.boot.common.utils.context.ContextUtils
 *  org.apache.commons.lang3.ArrayUtils
 *  org.aspectj.lang.JoinPoint
 *  org.aspectj.lang.ProceedingJoinPoint
 *  org.aspectj.lang.annotation.Around
 *  org.aspectj.lang.annotation.Aspect
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.spel.support.StandardEvaluationContext
 */
package com.kuma.boot.idempotent.idempotentenhance.core.aspect;

import com.kuma.boot.core.utils.context.ContextUtils;
import com.kuma.boot.idempotent.idempotentenhance.core.annotation.Idempotent;
import com.kuma.boot.idempotent.idempotentenhance.core.helper.IdempotentHelper;
import java.lang.reflect.Method;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@Aspect
public class IdempotentAspect {
    @Around(value="@annotation(idempotent)")
    public Object idempotentAround(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        String source = idempotent.source();
        String operationType = idempotent.operationType();
        String businessKey = idempotent.businessKey();
        Method method = IdempotentAnnotationProvider.getMethod((JoinPoint)joinPoint);
        Object[] paramNames = IdempotentAnnotationProvider.getParameterNames(method);
        if (ArrayUtils.isNotEmpty((Object[])paramNames)) {
            EvaluationContext context = this.buildContext((String[])paramNames, joinPoint.getArgs());
            source = IdempotentAnnotationProvider.parse(idempotent.source(), context);
            operationType = IdempotentAnnotationProvider.parse(idempotent.operationType(), context);
            businessKey = IdempotentAnnotationProvider.parse(idempotent.businessKey(), context);
        }
        IdempotentHelper idempotentHelper = (IdempotentHelper)ContextUtils.getBean(IdempotentHelper.class);
        try {
            return idempotentHelper.invoke(source, operationType, businessKey, method.getGenericReturnType(), () -> ((ProceedingJoinPoint)joinPoint).proceed());
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private EvaluationContext buildContext(String[] paramNames, Object[] paramValues) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        if (paramNames == null || paramValues == null) {
            return context;
        }
        for (int i = 0; i < paramValues.length; ++i) {
            context.setVariable(paramNames[i], paramValues[i]);
        }
        return context;
    }
}

