/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.aop.aop.log;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(value={ElementType.TYPE, ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface LogExecutionElapsed {
    public TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}

