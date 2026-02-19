/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.PARAMETER, ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface VerifyParameters {
    public String startTimeParamName() default "startTime";

    public String endTimeParamName() default "endTime";

    public String paramName() default "";
}

