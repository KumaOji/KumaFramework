/*
 *  org.aspectj.lang.JoinPoint
 *  org.aspectj.lang.reflect.MethodSignature
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.boot.convert.DurationStyle
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.ratelimit.ratelimitprovider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.util.StringUtils;

public class RateLimiterService {
    private static final String NAME_PREFIX = "RATE_LIMITER_";
    private static final Logger logger = LoggerFactory.getLogger(RateLimiterService.class);
    private final BizKeyProvider bizKeyProvider;

    public RateLimiterService(BizKeyProvider bizKeyProvider) {
        this.bizKeyProvider = bizKeyProvider;
    }

    RateLimiterInfo getRateLimiterInfo(JoinPoint joinPoint, RateLimit rateLimit) {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        String businessKeyName = this.bizKeyProvider.getKeyName(joinPoint, rateLimit);
        String rateLimitKey = this.getName(signature) + businessKeyName;
        if (StringUtils.hasLength((String)rateLimit.keyFunction())) {
            try {
                rateLimitKey = this.getName(signature) + this.executeFunction(rateLimit.keyFunction(), joinPoint).toString();
            }
            catch (Throwable throwable) {
                logger.info("Gets the custom Key exception and degrades it to the default Key:{}", (Object)rateLimit, (Object)throwable);
            }
        }
        long rate = this.bizKeyProvider.getRateValue(rateLimit);
        long rateInterval = DurationStyle.detectAndParse((String)rateLimit.rateInterval()).getSeconds();
        return new RateLimiterInfo(rateLimitKey, rate, rateInterval);
    }

    public Object executeFunction(String fallbackName, JoinPoint joinPoint) throws Throwable {
        Object res;
        Method handleMethod;
        Method currentMethod = ((MethodSignature)joinPoint.getSignature()).getMethod();
        Object target = joinPoint.getTarget();
        try {
            handleMethod = joinPoint.getTarget().getClass().getDeclaredMethod(fallbackName, currentMethod.getParameterTypes());
            handleMethod.setAccessible(true);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Illegal annotation param customLockTimeoutStrategy", e);
        }
        Object[] args = joinPoint.getArgs();
        try {
            res = handleMethod.invoke(target, args);
        }
        catch (IllegalAccessException e) {
            throw new ExecuteFunctionException("Fail to invoke custom lock timeout handler: " + fallbackName, e);
        }
        catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
        return res;
    }

    private String getName(MethodSignature signature) {
        return NAME_PREFIX + String.format("%s.%s", signature.getDeclaringTypeName(), signature.getMethod().getName());
    }
}

