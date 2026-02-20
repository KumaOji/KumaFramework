/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.validation.Constraint
 *  jakarta.validation.Payload
 */
package com.kuma.boot.web.validation.annotation;

import com.kuma.boot.web.validation.validator.FlagValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy={FlagValueValidator.class})
@Target(value={ElementType.FIELD, ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface FlagValue {
    public String message() default "\u4e0d\u6b63\u786e\u7684flag\u6807\u8bc6\uff0c\u8bf7\u4f20\u9012Y\u6216\u8005N";

    public Class[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

    public boolean required() default true;

    @Target(value={ElementType.FIELD, ElementType.PARAMETER})
    @Retention(value=RetentionPolicy.RUNTIME)
    @Documented
    public static @interface List {
        public FlagValue[] value();
    }
}

