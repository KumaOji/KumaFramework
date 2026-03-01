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

package com.kuma.boot.ratelimit.ratelimitguava;

import com.google.common.util.concurrent.RateLimiter;
import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.utils.log.LogUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Guava限制aop
 *
 * @author kuma
 * @version 2022.08
 * @since 2022-08-08 10:33:24
 */
@Aspect
public class GuavaLimitAspect {

    private final ConcurrentHashMap<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

    @Around(value = "@annotation(guavaLimit)")
    public Object around(ProceedingJoinPoint joinPoint, com.kuma.boot.ratelimit.ratelimitguava.GuavaLimit guavaLimit) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        RateLimiter rateLimiter = rateLimiterMap.get(methodSignature.toString());

        if (rateLimiter == null) {
            rateLimiter = RateLimiter.create(guavaLimit.token());
            rateLimiterMap.put(methodSignature.toString(), rateLimiter);
        }

        // 开始限流
        if (!rateLimiter.tryAcquire()) {
            return guavaLimit.message();
        }

        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            LogUtils.error(e);
            throw new GuavaLimitException(ResultEnum.FAILED);
        }
    }
}
