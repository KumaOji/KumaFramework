/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.validation.Constraint
 *  jakarta.validation.Payload
 */
package com.kuma.boot.web.validation.annotation;

import com.kuma.boot.web.validation.validator.PhoneValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy={PhoneValueValidator.class})
@Target(value={ElementType.FIELD, ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface PhoneValue {
    public String message() default "\u624b\u673a\u53f7\u7801\u683c\u5f0f\u4e0d\u6b63\u786e";

    public Class[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

    public boolean required() default true;

    @Target(value={ElementType.FIELD, ElementType.PARAMETER})
    @Retention(value=RetentionPolicy.RUNTIME)
    @Documented
    public static @interface List {
        public PhoneValue[] value();
    }
}

