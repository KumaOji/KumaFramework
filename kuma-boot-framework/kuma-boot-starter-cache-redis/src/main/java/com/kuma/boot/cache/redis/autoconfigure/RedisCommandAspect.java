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

package com.kuma.boot.cache.redis.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Arrays;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

// 控Redis命令流可能会对性能产生一定影响，尤其是使用MONITOR命令时。建议在开发或调试环境中启用实时命令流监控，
// 生产环境中应避免长时间启用MONITOR，从而避免影响到Redis的操作。
// 使用AOP方式监控RedisTemplate方法时，虽然不会影响Redis本身，
// 但会对应用的性能产生一定的开销，因此应根据需要调整日志级别，避免打印过多的日志信息。
// @Aspect
// @Component
/**
 * RedisCommandAspect
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class RedisCommandAspect {

    @Around("execution(* org.springframework.data.redis.core.RedisTemplate.*(..))")
    public Object monitorRedisCommands(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        LogUtils.info("Executing Redis command: {} with args: {}", methodName, Arrays.toString(args));
        return joinPoint.proceed();
    }
}
