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
public @interface AtlasLogHttpLog {
    public boolean logFullParameters() default true;

    public String urlFormat() default "";

    public boolean includeQueryString() default true;

    public boolean includeHeaders() default false;

    public String[] excludeHeaders() default {"authorization", "cookie", "x-auth-token"};
}

