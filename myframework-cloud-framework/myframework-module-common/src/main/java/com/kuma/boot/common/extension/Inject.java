/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.extension;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE, ElementType.METHOD})
public @interface Inject {
    public boolean enable() default true;

    public InjectType type() default InjectType.ByName;

    public static enum InjectType {
        ByName,
        ByType;

    }
}

