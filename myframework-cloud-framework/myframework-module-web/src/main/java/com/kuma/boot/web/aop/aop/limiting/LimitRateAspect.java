/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.util.concurrent.RateLimiter
 *  org.aspectj.lang.ProceedingJoinPoint
 *  org.aspectj.lang.annotation.Around
 *  org.aspectj.lang.annotation.Aspect
 *  org.aspectj.lang.reflect.MethodSignature
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.web.aop.aop.limiting;

import com.google.common.util.concurrent.RateLimiter;
import com.kuma.boot.web.aop.aop.util.SpringExpressionUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LimitRateAspect {
    private Map<String, RateLimiter> rateLimiters = new ConcurrentHashMap<String, RateLimiter>();

    @Around(value="@annotation(limitRate) || @within(limitRate)")
    public Object around(ProceedingJoinPoint joinPoint, LimitRate limitRate) throws Throwable {
        String key = SpringExpressionUtils.parse(limitRate.spel(), ((MethodSignature)joinPoint.getSignature()).getMethod(), joinPoint.getArgs(), String.class);
        RateLimiter rateLimiter = this.rateLimiters.get(key);
        if (rateLimiter != null) {
            rateLimiter = this.rateLimiters.computeIfAbsent(key, k -> RateLimiter.create((double)limitRate.qps()));
        }
        if (rateLimiter.tryAcquire()) {
            return joinPoint.proceed();
        }
        throw new RateLimitedException("Access limited, key=" + key + ", qps=" + limitRate.qps());
    }
}

