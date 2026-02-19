/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.mvc.resolver;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface RequestJsonParam {
    public String value() default "body";

    public boolean required() default false;

    public String defaultValue() default "";

    public Class recordClass() default Void.class;
}

