/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.ratelimit.ratelimitprovider;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 速度限制器服务
 *
 * @author kuma
 * @version 2022.09
 * @since 2022-10-26 08:56:39
 */
public class RateLimiterService {

    private static final String NAME_PREFIX = "RATE_LIMITER_";
    private static final Logger logger = LoggerFactory.getLogger(RateLimiterService.class);

    private final com.kuma.boot.ratelimit.ratelimitprovider.BizKeyProvider bizKeyProvider;

    public RateLimiterService(com.kuma.boot.ratelimit.ratelimitprovider.BizKeyProvider bizKeyProvider) {
        this.bizKeyProvider = bizKeyProvider;
    }

    com.kuma.boot.ratelimit.ratelimitprovider.RateLimiterInfo getRateLimiterInfo(JoinPoint joinPoint, com.kuma.boot.ratelimit.ratelimitprovider.RateLimit rateLimit) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String businessKeyName = bizKeyProvider.getKeyName(joinPoint, rateLimit);
        String rateLimitKey = getName(signature) + businessKeyName;
        if (StringUtils.hasLength(rateLimit.keyFunction())) {
            try {
                rateLimitKey = getName(signature)
                        + this.executeFunction(rateLimit.keyFunction(), joinPoint)
                        .toString();
            } catch (Throwable throwable) {
                logger.info(
                        "Gets the custom Key exception and degrades it to the default Key:{}", rateLimit, throwable);
            }
        }
        long rate = bizKeyProvider.getRateValue(rateLimit);
        long rateInterval =
                DurationStyle.detectAndParse(rateLimit.rateInterval()).getSeconds();
        return new com.kuma.boot.ratelimit.ratelimitprovider.RateLimiterInfo(rateLimitKey, rate, rateInterval);
    }

    /** 执行自定义函数 */
    public Object executeFunction(String fallbackName, JoinPoint joinPoint) throws Throwable {
        // prepare invocation context
        Method currentMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object target = joinPoint.getTarget();
        Method handleMethod;
        try {
            handleMethod =
                    joinPoint.getTarget().getClass().getDeclaredMethod(fallbackName, currentMethod.getParameterTypes());
            handleMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Illegal annotation param customLockTimeoutStrategy", e);
        }
        Object[] args = joinPoint.getArgs();

        // invoke
        Object res;
        try {
            res = handleMethod.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new com.kuma.boot.ratelimit.ratelimitprovider.ExecuteFunctionException("Fail to invoke custom lock timeout handler: " + fallbackName, e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }

        return res;
    }

    /** 获取基础的限流 key */
    private String getName(MethodSignature signature) {
        return NAME_PREFIX
                + String.format(
                "%s.%s",
                signature.getDeclaringTypeName(), signature.getMethod().getName());
    }
}
