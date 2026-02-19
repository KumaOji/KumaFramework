/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.exception.advice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface ErrorMapping {
    public Class<? extends Exception> exception();

    public String code();

    public String message();

    public int httpStatus() default 500;
}

