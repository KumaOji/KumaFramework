/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.validation.ConstraintValidator
 *  jakarta.validation.ConstraintValidatorContext
 */
package com.kuma.boot.web.validation.validator;

import com.kuma.boot.web.validation.annotation.JoinLength;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class JoinLengthValidator
implements ConstraintValidator<JoinLength, String> {
    private String symbol;
    private int limitSize;

    public void initialize(JoinLength constraintAnnotation) {
        this.symbol = constraintAnnotation.symbol();
        this.limitSize = constraintAnnotation.limitSize();
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return value.split(this.symbol).length <= this.limitSize;
    }
}

