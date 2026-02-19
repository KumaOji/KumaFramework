/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.aspectj.lang.ProceedingJoinPoint
 *  org.aspectj.lang.annotation.Around
 *  org.aspectj.lang.annotation.Aspect
 *  org.aspectj.lang.reflect.MethodSignature
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.web.aop.aop.caching;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.aop.aop.util.SpringExpressionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class CachingAspect {
    @Autowired(required=false)
    private CacheProvider cacheProvider;

    @Around(value="@annotation(caching)")
    public Object around(ProceedingJoinPoint joinPoint, Caching caching) throws Throwable {
        if (this.cacheProvider == null) {
            return joinPoint.proceed();
        }
        Object value = SpringExpressionUtils.parse(caching.value(), ((MethodSignature)joinPoint.getSignature()).getMethod(), joinPoint.getArgs(), Object.class);
        if (value == null) {
            throw new NullPointerException("parse caching key expression result is null");
        }
        String key = value.toString();
        Object obj = this.cacheProvider.get(key, caching.expireMillis());
        if (obj == null) {
            LogUtils.debug((String)"caching key {} missed!", (Object[])new Object[]{key});
            obj = joinPoint.proceed();
            if (obj != null || caching.cacheIfNull()) {
                this.cacheProvider.put(key, obj, caching.expireMillis());
            }
        } else {
            LogUtils.debug((String)"caching key {} hit!", (Object[])new Object[]{key});
        }
        return obj;
    }
}

