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

package com.kuma.boot.web.aop.aop.log;

import com.google.common.base.Stopwatch;
import com.kuma.boot.common.utils.log.LogUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * 记录执行耗时
 */
@Component
@Aspect
public class LogExecutionElapsedAspect {

    /**
     * 记录执行耗时
     * @param joinPoint 连接点
     * @return 方法返回
     * @throws Throwable 方法抛出的异常
     */
    @Around("@annotation(logExecutionElapsed) || @within(logExecutionElapsed)")
    public Object around(ProceedingJoinPoint joinPoint, com.kuma.boot.web.aop.aop.log.LogExecutionElapsed logExecutionElapsed)
            throws Throwable {
        /*String spanName = MDC.get("spanName");
        if (StringUtils.isBlank(spanName)) {
            spanName = String.format("%s#%s", joinPoint.getTarget().getClass().getSimpleName(), ((MethodSignature) joinPoint.getSignature()).getMethod().getName());
            MDC.put("spanName", spanName);
        }*/
        String spanName =
                String.format(
                        "%s#%s",
                        joinPoint.getTarget().getClass().getSimpleName(),
                        ((MethodSignature) joinPoint.getSignature()).getMethod().getName());
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            Object result = joinPoint.proceed();
            long elapsed = stopwatch.elapsed(logExecutionElapsed.timeUnit());
            // MDC.put("elapsed", String.valueOf(elapsed));
            LogUtils.info("{} elapsed {} {}", spanName, elapsed, logExecutionElapsed.timeUnit());
            return result;
        } catch (Throwable t) {
            // long elapsed = stopwatch.elapsed(logExecutionElapsed.timeUnit());
            // MDC.put("elapsed", String.valueOf(elapsed));
            LogUtils.error("{} throws ", spanName, t);
            throw t;
        } finally {
            // MDC.remove("elapsed");
        }
    }
}
