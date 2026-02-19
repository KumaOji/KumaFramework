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
package com.kuma.boot.web.aop;

import com.google.common.base.Stopwatch;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.annotation.CountTime;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CountTimeAop {
    @Around(value="@annotation(countTime)")
    public Object doAround(ProceedingJoinPoint pjp, CountTime countTime) throws Throwable {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Object obj = pjp.proceed();
        MethodSignature signature = (MethodSignature)pjp.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();
        long elapsed = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        LogUtils.info((String)"\u65b9\u6cd5 [{}] \u82b1\u8d39\u65f6\u95f4\uff1a{}ms", (Object[])new Object[]{methodName, elapsed});
        return obj;
    }
}

