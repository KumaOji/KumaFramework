/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitprovider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface RateLimit {
    public long rate();

    public String rateExpression() default "";

    public String rateInterval();

    public String[] keys() default {};

    public String keyFunction() default "";

    public String fallbackFunction() default "";
}

