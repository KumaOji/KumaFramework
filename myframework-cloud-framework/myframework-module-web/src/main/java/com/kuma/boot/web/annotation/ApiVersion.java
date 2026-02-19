/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE, ElementType.METHOD})
public @interface ApiVersion {
    public double value() default 1.0;

    public boolean deprecated() default false;
}

