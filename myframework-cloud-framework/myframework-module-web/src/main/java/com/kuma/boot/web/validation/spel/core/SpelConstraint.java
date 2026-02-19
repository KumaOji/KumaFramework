/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(value={ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface SpelConstraint {
    public Class<? extends SpelConstraintValidator<?>> validatedBy();
}

