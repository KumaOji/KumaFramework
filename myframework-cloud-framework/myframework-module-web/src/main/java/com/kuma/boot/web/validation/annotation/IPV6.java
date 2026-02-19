/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.validation.Constraint
 *  jakarta.validation.Payload
 */
package com.kuma.boot.web.validation.annotation;

import com.kuma.boot.web.validation.validator.IPV6Validator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Constraint(validatedBy={IPV6Validator.class})
@Repeatable(value=List.class)
public @interface IPV6 {
    public boolean notNull() default true;

    public String message() default "\u4e0d\u662f\u4e00\u4e2a\u5408\u6cd5\u7684IPV6\u5730\u5740";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

    @Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(value=RetentionPolicy.RUNTIME)
    @Documented
    public static @interface List {
        public IPV6[] value();
    }
}

