/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.spider;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CssQuery {
    public static final int DEFAULT_REGEX_GROUP = 0;

    public String value();

    public String attr() default "";

    public String regex() default "";

    public int regexGroup() default 0;

    public boolean inner() default false;
}

