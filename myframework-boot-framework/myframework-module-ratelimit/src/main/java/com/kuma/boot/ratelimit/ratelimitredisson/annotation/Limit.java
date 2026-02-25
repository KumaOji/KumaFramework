/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitredisson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Limit {
    public String key() default "";

    public int rateInterval();

    public TimeUnit rateIntervalUnit() default TimeUnit.SECONDS;

    public int rate();

    public boolean restrictIp() default false;
}

