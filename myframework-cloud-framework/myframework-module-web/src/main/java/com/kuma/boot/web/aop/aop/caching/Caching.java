/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.aop.aop.caching;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface Caching {
    public String value();

    public long expireMillis() default 30000L;

    public boolean cacheIfNull() default false;
}

