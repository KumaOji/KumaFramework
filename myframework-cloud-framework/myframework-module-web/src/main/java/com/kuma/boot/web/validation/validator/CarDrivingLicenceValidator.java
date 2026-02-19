/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.lang.Validator
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  jakarta.validation.ConstraintValidator
 *  jakarta.validation.ConstraintValidatorContext
 */
package com.kuma.boot.web.validation.validator;

import cn.hutool.core.lang.Validator;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.web.validation.annotation.CarDrivingLicence;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CarDrivingLicenceValidator
implements ConstraintValidator<CarDrivingLicence, String> {
    private boolean notNull;

    public void initialize(CarDrivingLicence constraintAnnotation) {
        this.notNull = constraintAnnotation.notNull();
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isNotBlank((String)value)) {
            return Validator.isCarDrivingLicence((CharSequence)value);
        }
        return !this.notNull;
    }
}

