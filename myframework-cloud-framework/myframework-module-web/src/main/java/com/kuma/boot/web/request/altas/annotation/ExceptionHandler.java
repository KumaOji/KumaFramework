/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.request.altas.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface ExceptionHandler {
    public Class<? extends Throwable> exception();

    public LogLevel level() default LogLevel.ERROR;

    public String message() default "";

    public boolean logStackTrace() default true;
}

