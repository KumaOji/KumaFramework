/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.validation.Constraint
 *  org.intellij.lang.annotations.Language
 */
package com.kuma.boot.web.validation.spel.jakarta;

import jakarta.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.intellij.lang.annotations.Language;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Constraint(validatedBy={SpelValidator.class})
public @interface SpelValid {
    @Language(value="SpEL")
    public String condition() default "";

    @Language(value="SpEL")
    public String[] spelGroups() default {};

    public String message() default "";

    public Class<?>[] groups() default {};

    public Class<?>[] payload() default {};
}

