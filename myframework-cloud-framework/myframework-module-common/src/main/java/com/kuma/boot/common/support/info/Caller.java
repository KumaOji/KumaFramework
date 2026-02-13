/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.info;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface Caller {
    public String contacts();

    public String desc();

    public String sys();

    public String use();
}

