/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.date.DateUtils
 *  jakarta.validation.ConstraintValidator
 *  jakarta.validation.ConstraintValidatorContext
 */
package com.kuma.boot.web.validation.validator;

import com.kuma.boot.common.utils.date.DateUtils;
import com.kuma.boot.web.validation.annotation.DateValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateValueValidator
implements ConstraintValidator<DateValue, String> {
    private String format;

    public void initialize(DateValue constraintAnnotation) {
        this.format = constraintAnnotation.format();
    }

    public boolean isValid(String dateValue, ConstraintValidatorContext context) {
        try {
            DateUtils.parseLocalDate((String)dateValue, (String)this.format);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}

