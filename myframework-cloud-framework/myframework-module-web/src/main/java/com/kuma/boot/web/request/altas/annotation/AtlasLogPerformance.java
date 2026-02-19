/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.request.altas.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface AtlasLogPerformance {
    public boolean enabled() default true;

    public long slowThreshold() default 1000L;

    public boolean logSlowMethods() default true;
}

