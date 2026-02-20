/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.validation.Constraint
 *  jakarta.validation.Payload
 */
package com.kuma.boot.web.validation.annotation;

import com.kuma.boot.web.validation.validator.RangeCompareValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Constraint(validatedBy={RangeCompareValidator.class})
@Documented
public @interface RangeCompare {
    public String message() default "\u8d77\u59cb\u503c\u5fc5\u987b\u5c0f\u4e8e\u6216\u7b49\u4e8e\u7ed3\u675f\u503c";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

    public String from();

    public String to();

    @Target(value={ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(value=RetentionPolicy.RUNTIME)
    @Documented
    public static @interface List {
        public RangeCompare[] value();
    }
}

