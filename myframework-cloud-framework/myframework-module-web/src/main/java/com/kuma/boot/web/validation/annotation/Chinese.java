/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.validation.Constraint
 *  jakarta.validation.Payload
 */
package com.kuma.boot.web.validation.annotation;

import com.kuma.boot.web.validation.validator.ChineseValidator;
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
@Constraint(validatedBy={ChineseValidator.class})
@Repeatable(value=List.class)
public @interface Chinese {
    public boolean notNull() default true;

    public String message() default "\u5fc5\u987b\u662f\u4e2d\u6587\u6c49\u5b57";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

    @Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(value=RetentionPolicy.RUNTIME)
    @Documented
    public static @interface List {
        public Chinese[] value();
    }
}

