/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.support.methodPartAndRetryer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface MethodPartAndRetryer {
    public int times() default 3;

    public long waitTime() default 300L;

    public int parts() default 200;
}

