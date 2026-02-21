/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.validation.Constraint
 *  jakarta.validation.Payload
 */
package com.kuma.boot.ip2region.ip2region.annotation;

import com.kuma.boot.ip2region.ip2region.IPValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
@Constraint(validatedBy={IPValidator.class})
public @interface IP {
    public String message() default "IP\u4e0d\u7b26\u5408\u683c\u5f0f";

    public boolean dns() default true;

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

    public static interface Constant {
        public static final String HTTPS = "https://";
        public static final String HTTP = "http://";
    }
}

