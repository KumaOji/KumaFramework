/*
 *  com.google.common.util.concurrent.RateLimiter
 *  com.kuma.boot.common.enums.ResultEnum
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.aspectj.lang.ProceedingJoinPoint
 *  org.aspectj.lang.annotation.Around
 *  org.aspectj.lang.annotation.Aspect
 *  org.aspectj.lang.reflect.MethodSignature
 */
package com.kuma.boot.ratelimit.ratelimitguava;

import com.google.common.util.concurrent.RateLimiter;
import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.utils.log.LogUtils;

import java.util.concurrent.ConcurrentHashMap;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class GuavaLimitAspect {
    private final ConcurrentHashMap<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap();

    @Around(value="@annotation(guavaLimit)")
    public Object around(ProceedingJoinPoint joinPoint, GuavaLimit guavaLimit) throws Throwable {
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        RateLimiter rateLimiter = this.rateLimiterMap.get(methodSignature.toString());
        if (rateLimiter == null) {
            rateLimiter = RateLimiter.create((double)guavaLimit.token());
            this.rateLimiterMap.put(methodSignature.toString(), rateLimiter);
        }
        if (!rateLimiter.tryAcquire()) {
            return guavaLimit.message();
        }
        try {
            return joinPoint.proceed();
        }
        catch (Throwable e) {
            LogUtils.error((Throwable)e);
            throw new GuavaLimitException(ResultEnum.FAILED);
        }
    }
}

