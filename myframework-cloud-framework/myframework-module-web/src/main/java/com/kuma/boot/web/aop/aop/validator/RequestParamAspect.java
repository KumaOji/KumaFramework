/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.aspectj.lang.JoinPoint
 *  org.aspectj.lang.annotation.Aspect
 *  org.aspectj.lang.annotation.Before
 *  org.aspectj.lang.annotation.Pointcut
 *  org.springframework.context.support.DefaultMessageSourceResolvable
 *  org.springframework.stereotype.Component
 *  org.springframework.validation.BindingResult
 *  org.springframework.validation.Errors
 */
package com.kuma.boot.web.aop.aop.validator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

@Component
@Aspect
public class RequestParamAspect {
    @Pointcut(value="@annotation(com.kuma.boot.web.aop.aop.validator.ValidateParameter) || @within(com.kuma.boot.web.aop.aop.validator.ValidateParameter)")
    public void pointcut() {
    }

    @Before(value="pointcut()")
    public void before(JoinPoint joinPoint) {
        Arrays.stream(joinPoint.getArgs()).filter(Objects::nonNull).filter(x -> BindingResult.class.isAssignableFrom(x.getClass())).map(x -> (BindingResult)x).filter(Errors::hasErrors).map(Errors::getFieldErrors).flatMap(Collection::stream).findFirst().map(DefaultMessageSourceResolvable::getDefaultMessage).ifPresent(x -> {
            throw new ParameterValidateException((String)x);
        });
    }
}

