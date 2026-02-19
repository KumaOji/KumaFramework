/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.cache.redis.delay.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface RedissonListener {
    public String[] queues();

    public String errorHandler() default "";

    public String isolationStrategy() default "";

    public String messageConverter() default "";

    public int concurrency() default 1;

    public int maxFetch() default 1;
}

