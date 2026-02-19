/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.validation.Constraint
 *  jakarta.validation.Payload
 */
package com.kuma.boot.web.validation.annotation;

import com.kuma.boot.web.validation.validator.StatusValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy={StatusValueValidator.class})
@Target(value={ElementType.FIELD, ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface StatusValue {
    public String message() default "\u4e0d\u6b63\u786e\u7684\u72b6\u6001\u6807\u8bc6";

    public Class[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

    public boolean required() default true;

    @Target(value={ElementType.FIELD, ElementType.PARAMETER})
    @Retention(value=RetentionPolicy.RUNTIME)
    @Documented
    public static @interface List {
        public StatusValue[] value();
    }
}

