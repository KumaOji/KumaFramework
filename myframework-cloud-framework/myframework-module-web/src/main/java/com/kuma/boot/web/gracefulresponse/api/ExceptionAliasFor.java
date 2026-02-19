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
public @interface ExceptionAliasFor {
    public String code() default "1";

    public String msg() default "error";

    public Class<? extends Throwable>[] aliasFor();
}

