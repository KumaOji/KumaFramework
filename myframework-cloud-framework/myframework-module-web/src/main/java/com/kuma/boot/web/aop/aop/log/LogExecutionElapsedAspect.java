/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.aspectj.lang.ProceedingJoinPoint
 *  org.aspectj.lang.annotation.Around
 *  org.aspectj.lang.annotation.Aspect
 *  org.aspectj.lang.reflect.MethodSignature
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.web.aop.aop.log;

import com.google.common.base.Stopwatch;
import com.kuma.boot.common.utils.log.LogUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LogExecutionElapsedAspect {
    @Around(value="@annotation(logExecutionElapsed) || @within(logExecutionElapsed)")
    public Object around(ProceedingJoinPoint joinPoint, LogExecutionElapsed logExecutionElapsed) throws Throwable {
        String spanName = String.format("%s#%s", joinPoint.getTarget().getClass().getSimpleName(), ((MethodSignature)joinPoint.getSignature()).getMethod().getName());
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            Object result = joinPoint.proceed();
            long elapsed = stopwatch.elapsed(logExecutionElapsed.timeUnit());
            LogUtils.info((String)"{} elapsed {} {}", (Object[])new Object[]{spanName, elapsed, logExecutionElapsed.timeUnit()});
            Object object = result;
            return object;
        }
        catch (Throwable t) {
            LogUtils.error((String)"{} throws ", (Object[])new Object[]{spanName, t});
            throw t;
        }
    }
}

