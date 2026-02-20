/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.validation.ConstraintValidator
 *  jakarta.validation.ConstraintValidatorContext
 */
package com.kuma.boot.web.validation.validator;

import com.kuma.boot.web.validation.annotation.EnumValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;

public class EnumValueValidator
implements ConstraintValidator<EnumValue, Integer> {
    private Class<? extends Enum> enumClass;
    private static final String METHOD_NAME = "toEnum";

    public void initialize(EnumValue constraintAnnotation) {
        this.enumClass = constraintAnnotation.value();
        try {
            this.enumClass.getDeclaredMethod(METHOD_NAME, Integer.TYPE);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("the enum class has not toEnum method", e);
        }
    }

    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        Method declareMethod;
        try {
            declareMethod = this.enumClass.getDeclaredMethod(METHOD_NAME, Integer.TYPE);
        }
        catch (NoSuchMethodException e) {
            return false;
        }
        try {
            declareMethod.invoke(null, value);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
}

