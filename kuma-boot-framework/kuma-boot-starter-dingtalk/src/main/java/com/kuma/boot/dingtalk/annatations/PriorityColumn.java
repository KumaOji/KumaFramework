/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.annatations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(value=RetentionPolicy.SOURCE)
@Target(value={ElementType.FIELD})
public @interface PriorityColumn {
    public Class<?> clazz() default Void.class;

    public String column();

    public boolean priority() default false;
}

