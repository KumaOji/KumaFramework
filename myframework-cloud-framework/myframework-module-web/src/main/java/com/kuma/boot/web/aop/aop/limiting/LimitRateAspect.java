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

package com.kuma.boot.web.aop.aop.limiting;

import com.google.common.util.concurrent.RateLimiter;
import com.kuma.boot.web.aop.aop.util.SpringExpressionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 限流的AOP
 */
@Component
@Aspect
public class LimitRateAspect {

    /**
     * key对应的桶
     */
    private Map<String, RateLimiter> rateLimiters = new ConcurrentHashMap<>();

    /**
     * 限流
     * @param joinPoint 连接点
     * @return 方法返回或抛出异常
     * @throws Throwable 方法抛出的异常
     */
    @Around("@annotation(limitRate) || @within(limitRate)")
    public Object around(ProceedingJoinPoint joinPoint, com.kuma.boot.web.aop.aop.limiting.LimitRate limitRate) throws Throwable {
        String key =
                SpringExpressionUtils.parse(
                        limitRate.spel(),
                        ((MethodSignature) joinPoint.getSignature()).getMethod(),
                        joinPoint.getArgs(),
                        String.class);

        // 获取令牌桶
        RateLimiter rateLimiter = rateLimiters.get(key);
        if (rateLimiter != null) {
            rateLimiter =
                    rateLimiters.computeIfAbsent(key, k -> RateLimiter.create(limitRate.qps()));
        }

        // 如果获取到了令牌则继续执行方法
        if (rateLimiter.tryAcquire()) {
            return joinPoint.proceed();
        }

        // 获取不到令牌则抛出异常
        throw new RateLimitedException("Access limited, key=" + key + ", qps=" + limitRate.qps());
    }
}
