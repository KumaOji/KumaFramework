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

package com.kuma.boot.web.aop;

import com.google.common.base.Stopwatch;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.annotation.CountTime;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 方法耗时工具类
 *
 * @author kuma
 * @version 2022.09
 * @since 2022-10-27 10:18:35
 */
@Aspect
@Component
public class CountTimeAop {

    @Around("@annotation(countTime)")
    public Object doAround(ProceedingJoinPoint pjp, CountTime countTime) throws Throwable {
        // 创建的时候就开始计时
        Stopwatch stopwatch = Stopwatch.createStarted();
        Object obj = pjp.proceed();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();
        // 停止计时，然后计算时长.单位为毫秒.
        long elapsed = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        LogUtils.info("方法 [{}] 花费时间：{}ms", methodName, (elapsed));
        return obj;
    }
}
