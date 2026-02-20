/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.validation.ConstraintValidator
 *  jakarta.validation.ConstraintValidatorContext
 */
package com.kuma.boot.web.validation.validator;

import com.kuma.boot.web.validation.annotation.LimitedValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LimitedValueValidator
implements ConstraintValidator<LimitedValue, Object> {
    private String[] strValues;
    private int[] intValues;
    private boolean allowNullValue;

    public void initialize(LimitedValue constraintAnnotation) {
        this.strValues = constraintAnnotation.strValues();
        this.intValues = constraintAnnotation.intValues();
        this.allowNullValue = constraintAnnotation.allowNullValue();
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        block4: {
            block3: {
                if (value == null && this.allowNullValue) {
                    return true;
                }
                if (!(value instanceof String)) break block3;
                for (String s : this.strValues) {
                    if (!s.equals(value)) continue;
                    return true;
                }
                break block4;
            }
            if (!(value instanceof Integer)) break block4;
            int[] nArray = this.intValues;
            int n = nArray.length;
            for (int i = 0; i < n; ++i) {
                Integer s = nArray[i];
                if (s != value) continue;
                return true;
            }
        }
        return false;
    }
}

