/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.request.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface RequestLogger {
    public String value() default "";

    public boolean enabled() default true;

    public boolean controllerApiValue() default true;

    public boolean request() default true;

    public boolean requestByError() default true;

    public boolean response() default true;
}

