/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface RateLimits {
    public RateLimit[] value();
}

