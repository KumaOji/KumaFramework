/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.validation.Constraint
 *  jakarta.validation.Payload
 */
package com.kuma.boot.web.validation.annotation;

import com.kuma.boot.web.validation.validator.LimitedValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD, ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy={LimitedValueValidator.class})
public @interface LimitedValue {
    public String message() default "\u5fc5\u987b\u4e3a\u6307\u5b9a\u503c";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

    public boolean allowNullValue() default false;

    public String[] strValues() default {};

    public int[] intValues() default {};
}

