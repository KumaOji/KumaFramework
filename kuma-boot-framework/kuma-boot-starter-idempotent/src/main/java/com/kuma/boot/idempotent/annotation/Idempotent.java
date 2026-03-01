/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.idempotent.annotation;

import com.kuma.boot.idempotent.enums.IdempotentTypeEnum;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {
    public String key() default "";

    public String perFix();

    public IdempotentTypeEnum ideTypeEnum() default IdempotentTypeEnum.ALL;

    public long waitTime() default 0L;

    public long leaseTime() default -1L;

    public TimeUnit unit() default TimeUnit.SECONDS;
}

