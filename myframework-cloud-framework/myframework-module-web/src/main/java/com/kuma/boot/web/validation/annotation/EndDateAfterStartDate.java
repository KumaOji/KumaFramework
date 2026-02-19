/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.validation.Constraint
 *  jakarta.validation.Payload
 */
package com.kuma.boot.web.validation.annotation;

import com.kuma.boot.web.validation.validator.EndDateAfterStartDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Constraint(validatedBy={EndDateAfterStartDateValidator.class})
public @interface EndDateAfterStartDate {
    public String message() default "End date must be after start date";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};
}

