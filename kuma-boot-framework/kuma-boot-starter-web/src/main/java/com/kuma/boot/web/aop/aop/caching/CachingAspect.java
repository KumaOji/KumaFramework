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

package com.kuma.boot.web.aop.aop.caching;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.aop.aop.util.SpringExpressionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 缓存
 */
@Component
@Aspect
public class CachingAspect {

    @Autowired(required = false)
    private com.kuma.boot.web.aop.aop.caching.CacheProvider cacheProvider;

    /**
     * 缓存
     * @param joinPoint 连接点
     * @return 方法返回
     * @throws Throwable 方法抛出的异常
     */
    @Around("@annotation(caching)")
    public Object around(ProceedingJoinPoint joinPoint, com.kuma.boot.web.aop.aop.caching.Caching caching) throws Throwable {
        if (cacheProvider == null) {
            return joinPoint.proceed();
        }

        Object value =
                SpringExpressionUtils.parse(
                        caching.value(),
                        ((MethodSignature) joinPoint.getSignature()).getMethod(),
                        joinPoint.getArgs(),
                        Object.class);

        if (value == null) {
            throw new NullPointerException("parse caching key expression result is null");
        }

        String key = value.toString();

        // 先从缓存获取
        Object obj = cacheProvider.get(key, caching.expireMillis());

        if (obj == null) {
            LogUtils.debug("caching key {} missed!", key);
            obj = joinPoint.proceed();
            if (obj != null || caching.cacheIfNull()) {
                cacheProvider.put(key, obj, caching.expireMillis());
            }
        } else {
            LogUtils.debug("caching key {} hit!", key);
        }

        return obj;
    }
}
