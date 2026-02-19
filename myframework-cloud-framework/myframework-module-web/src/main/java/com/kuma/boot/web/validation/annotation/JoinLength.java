/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.validation.Constraint
 *  jakarta.validation.Payload
 */
package com.kuma.boot.web.validation.annotation;

import com.kuma.boot.web.validation.validator.JoinLengthValidator;
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
@Constraint(validatedBy={JoinLengthValidator.class})
public @interface JoinLength {
    public String message() default "\u62fc\u63a5\u6570\u91cf\u957f\u5ea6\u4e0d\u5408\u6cd5";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

    public String symbol() default ",";

    public int limitSize() default 5;
}

