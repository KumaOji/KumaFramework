/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.gracefulresponse.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface ExceptionMapper {
    public String code() default "ERROR";

    public String msg() default "Poor network quality!";

    public boolean msgReplaceable() default false;
}

