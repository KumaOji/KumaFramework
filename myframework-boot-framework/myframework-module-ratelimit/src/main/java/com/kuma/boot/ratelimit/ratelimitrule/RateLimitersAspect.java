/*
 *  cn.hutool.core.lang.Snowflake
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  jakarta.servlet.http.HttpServletRequest
 *  org.aspectj.lang.JoinPoint
 *  org.aspectj.lang.annotation.Aspect
 *  org.aspectj.lang.annotation.Before
 *  org.aspectj.lang.reflect.MethodSignature
 *  org.springframework.core.annotation.Order
 *  org.springframework.data.redis.core.RedisTemplate
 *  org.springframework.data.redis.core.script.RedisScript
 *  org.springframework.stereotype.Component
 *  org.springframework.web.context.request.RequestContextHolder
 *  org.springframework.web.context.request.ServletRequestAttributes
 */
package com.kuma.boot.ratelimit.ratelimitrule;

import cn.hutool.core.lang.Snowflake;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Order(value=20)
@Component
public class RateLimitersAspect {
    private final RedisRepository redisUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisScript<Boolean> limitScript;
    private final Snowflake snowflake;

    public RateLimitersAspect(RedisRepository redisUtil, RedisTemplate<String, Object> redisTemplate, RedisScript<Boolean> limitScript, Snowflake snowflake) {
        this.redisUtil = redisUtil;
        this.redisTemplate = redisTemplate;
        this.limitScript = limitScript;
        this.snowflake = snowflake;
    }

    @Before(value="@annotation(rateLimiters)")
    public void boBefore(JoinPoint joinPoint, RateLimiters rateLimiters) {
        RateLimiter[] limiters;
        for (RateLimiter rateLimiter : limiters = rateLimiters.rateLimiters()) {
        }
    }

    private String generateLimiterKey(JoinPoint joinPoint, RateLimiter limiter) {
        StringBuilder key = new StringBuilder("");
        switch (limiter.limitTypeEnum()) {
            case IP: {
                HttpServletRequest httpServletRequest = ((ServletRequestAttributes)Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
                break;
            }
            case USER_ID: {
                break;
            }
            case USER_NAME: {
                break;
            }
        }
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = method.getDeclaringClass();
        key.append(targetClass.getSimpleName()).append(":").append(method.getName());
        return key.toString();
    }

    private Object[] getRules(RateLimiter limiter) {
        RateRule[] rateRules = limiter.rateRules();
        Object[] result = new Object[rateRules.length * 2];
        for (int i = 0; i < rateRules.length; ++i) {
            result[i * 2] = rateRules[i].limit();
            result[i * 2 + 1] = rateRules[i].timeUnit().toMillis(rateRules[i].timeDuration());
        }
        return result;
    }

    private void addToRedisBlackList() {
        HttpServletRequest request = ((ServletRequestAttributes)Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }
}

