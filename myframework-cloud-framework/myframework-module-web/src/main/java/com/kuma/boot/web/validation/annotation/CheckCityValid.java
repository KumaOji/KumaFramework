/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.enums.CityTypeEnum
 *  jakarta.validation.Constraint
 *  jakarta.validation.Payload
 */
package com.kuma.boot.web.validation.annotation;

import com.kuma.boot.common.enums.CityTypeEnum;
import com.kuma.boot.web.validation.validator.CheckCityValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy={CheckCityValidator.class})
@Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface CheckCityValid {
    public CityTypeEnum value() default CityTypeEnum.CITY;

    public String message() default "{city.valid}";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};
}

