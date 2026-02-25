/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitguava;

import com.kuma.boot.ratelimit.ratelimitaspect.LimitType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Limit {
    public String name() default "";

    public String key() default "";

    public String prefix() default "";

    public int period();

    public int count();

    public LimitType limitType() default LimitType.CUSTOMER;
}

