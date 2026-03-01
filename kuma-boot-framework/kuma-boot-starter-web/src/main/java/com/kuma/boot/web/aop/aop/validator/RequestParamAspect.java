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

package com.kuma.boot.web.aop.aop.validator;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * 参数校验的切面
 */
@Component
@Aspect
public class RequestParamAspect {

    /**
     * 切入点 在类或方法上有该注解时切入
     */
    @Pointcut(
            "@annotation(com.kuma.boot.web.aop.aop.validator.ValidateParameter) || @within(com.kuma.boot.web.aop.aop.validator.ValidateParameter)")
    public void pointcut() {}

    /**
     * 方法执行前校验参数合法性
     * @param joinPoint 连接点
     */
    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        Arrays.stream(joinPoint.getArgs())
                .filter(Objects::nonNull)
                .filter(x -> BindingResult.class.isAssignableFrom(x.getClass()))
                .map(x -> (BindingResult) x)
                .filter(Errors::hasErrors)
                .map(Errors::getFieldErrors)
                .flatMap(Collection::stream)
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .ifPresent(
                        x -> {
                            throw new com.kuma.boot.web.aop.aop.validator.ParameterValidateException(x);
                        });
    }
}
