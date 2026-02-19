/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.request.altas.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Repeatable(value=Logs.class)
public @interface Log {
    public LogLevel level() default LogLevel.INFO;

    public String value() default "";

    public String condition() default "";

    public String[] tags() default {};

    public String group() default "default";

    public boolean logArgs() default false;

    public boolean logResult() default false;

    public boolean logExecutionTime() default true;

    public boolean logException() default true;

    public int[] excludeArgs() default {};

    public int maxArgLength() default 1000;

    public int maxResultLength() default 1000;

    public String enterMessage() default "";

    public String exitMessage() default "";

    public String exceptionMessage() default "";

    public ExceptionHandler[] exceptionHandlers() default {};

    public String argumentFormatter() default "";

    public String resultFormatter() default "";
}

