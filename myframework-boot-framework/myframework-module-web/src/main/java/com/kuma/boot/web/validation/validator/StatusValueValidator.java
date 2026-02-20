/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.validation.ConstraintValidator
 *  jakarta.validation.ConstraintValidatorContext
 */
package com.kuma.boot.web.validation.validator;

import com.kuma.boot.web.support.enums.StatusEnum;
import com.kuma.boot.web.validation.annotation.StatusValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StatusValueValidator
implements ConstraintValidator<StatusValue, String> {
    private Boolean required;

    public void initialize(StatusValue constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }

    public boolean isValid(String statusValue, ConstraintValidatorContext context) {
        if (this.required.booleanValue() && statusValue == null) {
            return false;
        }
        if (!this.required.booleanValue() && statusValue == null) {
            return true;
        }
        StatusEnum statusEnum = StatusEnum.codeToEnum(statusValue);
        return statusEnum != null;
    }
}

