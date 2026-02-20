/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.validation.ConstraintValidator
 *  jakarta.validation.ConstraintValidatorContext
 */
package com.kuma.boot.web.validation.validator;

import com.kuma.boot.web.validation.annotation.ListValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Optional;

public class ListValueValidator
implements ConstraintValidator<ListValue, String> {
    private String[] list;

    public void initialize(ListValue constraintAnnotation) {
        this.list = constraintAnnotation.listValue();
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        Optional<String> any = Arrays.stream(this.list).filter(obj -> obj.equals(value)).findAny();
        return any.isPresent();
    }
}

