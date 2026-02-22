/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.intellij.lang.annotations.Language
 *  org.springframework.security.access.prepost.PreAuthorize
 */
package com.kuma.boot.security.spring.access.expression;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.intellij.lang.annotations.Language;
import org.springframework.security.access.prepost.PreAuthorize;

@Target(value={ElementType.METHOD, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
@Documented
@PreAuthorize(value="@authorizeCheck.checkAuthority(#root)")
public @interface Authorize {
    public String[] value();

    @Language(value="SpEL")
    public String tenantId() default "";

    public boolean anyMatch() default true;
}

