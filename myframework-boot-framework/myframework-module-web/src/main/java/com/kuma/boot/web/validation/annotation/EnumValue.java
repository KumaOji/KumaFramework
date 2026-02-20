/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.validation.Constraint
 *  jakarta.validation.Payload
 */
package com.kuma.boot.web.validation.annotation;

import com.kuma.boot.web.validation.validator.EnumValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Constraint(validatedBy={EnumValueValidator.class})
@Repeatable(value=List.class)
public @interface EnumValue {
    public String message() default "the integer is not one of the enum values";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

    public Class<? extends Enum> value();

    @Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface List {
        public EnumValue[] value();
    }
}

