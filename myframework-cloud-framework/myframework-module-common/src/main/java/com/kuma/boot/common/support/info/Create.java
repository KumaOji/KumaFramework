/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.info;

import com.kuma.boot.common.support.info.ApiVersionEnum;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface Create {
    public ApiVersionEnum version();

    public String date();

    public String content() default "";

    public String createor() default "kuma";
}

