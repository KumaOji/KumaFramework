/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.core.annotation.AliasFor
 */
package com.kuma.boot.web.i18n;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target(value={ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface I18nField {
    @AliasFor(value="code")
    public String value() default "";

    @AliasFor(value="value")
    public String code() default "";

    public String condition() default "";
}

