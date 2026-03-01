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

package com.kuma.boot.ratelimit.ratelimitbs.spring.aop;

import com.kuma.boot.common.utils.aop.AopUtils;
import com.kuma.boot.ratelimit.ratelimitbs.core.bs.RateLimitBs;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * <p> 切面实现 </p>
 */
@Aspect
@Component
public class RateLimitAspect {

    private final RateLimitBs rateLimitBs;

    public RateLimitAspect(RateLimitBs rateLimitBs) {
        this.rateLimitBs = rateLimitBs;
    }

    /**
     * 指定注解的方法
     */
    @Pointcut(
            "@annotation(com.kuma.boot.ratelimit.ratelimitbs.core.annotation.RateLimit) || @annotation(com.kuma.boot.ratelimit.ratelimitbs.core.annotation.RateLimits)")
    public void methodMyPointcut() {}

    /**
     * 指定注解的类
     */
    @Pointcut(
            "@within(com.kuma.boot.ratelimit.ratelimitbs.core.annotation.RateLimit) || @within(com.kuma.boot.ratelimit.ratelimitbs.core.annotation.RateLimits)")
    public void classMyPointcut() {}

    /**
     * https://www.cnblogs.com/bjlhx/p/12081300.html 声明方式
     *
     * @param point 切面
     * @return 结果
     * @throws Throwable 异常
     */
    @Around("methodMyPointcut() || classMyPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // todo 需要修改
        Method method = AopUtils.getMethod(point);
        //        Method method = SpringAopUtil.getCurrentMethod(point);
        // 执行代理操作
        Object[] args = point.getArgs();

        // 核心处理方法
        rateLimitBs.tryAcquire(method, args);

        return point.proceed();
    }
}
