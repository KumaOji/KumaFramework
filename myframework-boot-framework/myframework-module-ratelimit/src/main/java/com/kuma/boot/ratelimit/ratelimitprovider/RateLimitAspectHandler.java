/*
 *  org.aspectj.lang.JoinPoint
 *  org.aspectj.lang.ProceedingJoinPoint
 *  org.aspectj.lang.annotation.Around
 *  org.aspectj.lang.annotation.Aspect
 *  org.redisson.api.RScript
 *  org.redisson.api.RScript$Mode
 *  org.redisson.api.RScript$ReturnType
 *  org.redisson.api.RedissonClient
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.annotation.Order
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.ratelimit.ratelimitprovider;

import java.util.ArrayList;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

@Aspect
@Order(value=0)
public class RateLimitAspectHandler {
    private static final Logger logger = LoggerFactory.getLogger(RateLimitAspectHandler.class);
    private final RateLimiterService rateLimiterService;
    private final RScript rScript;

    public RateLimitAspectHandler(RedissonClient client, RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
        this.rScript = client.getScript();
    }

    @Around(value="@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        boolean allowed;
        RateLimiterInfo limiterInfo = this.rateLimiterService.getRateLimiterInfo((JoinPoint)joinPoint, rateLimit);
        ArrayList<Object> keys = new ArrayList<Object>();
        keys.add(limiterInfo.getKey());
        keys.add(limiterInfo.getRate());
        keys.add(limiterInfo.getRateInterval());
        List results = (List)this.rScript.eval(RScript.Mode.READ_WRITE, LuaScript.getRateLimiterScript(), RScript.ReturnType.LIST, keys, new Object[0]);
        boolean bl = allowed = (Long)results.get(0) == 0L;
        if (!allowed) {
            logger.info("Trigger current limiting,key:{}", (Object)limiterInfo.getKey());
            if (StringUtils.hasLength((String)rateLimit.fallbackFunction())) {
                return this.rateLimiterService.executeFunction(rateLimit.fallbackFunction(), (JoinPoint)joinPoint);
            }
            long ttl = (Long)results.get(1);
            throw new RateLimitException("Too Many Requests", ttl);
        }
        return joinPoint.proceed();
    }
}

