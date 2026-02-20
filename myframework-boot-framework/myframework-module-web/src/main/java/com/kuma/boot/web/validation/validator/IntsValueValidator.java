/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.util.ArrayUtil
 *  jakarta.validation.ConstraintValidator
 *  jakarta.validation.ConstraintValidatorContext
 */
package com.kuma.boot.web.validation.validator;

import cn.hutool.core.util.ArrayUtil;
import com.kuma.boot.web.validation.annotation.IntsValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Objects;

public class IntsValueValidator
implements ConstraintValidator<IntsValue, Integer> {
    private int[] enumList;
    private IntsValue constraintAnnotation;

    public void initialize(IntsValue constraintAnnotation) {
        this.enumList = constraintAnnotation.value();
        this.constraintAnnotation = constraintAnnotation;
    }

    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(value) || ArrayUtil.contains((int[])this.enumList, (int)value)) {
            return true;
        }
        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate(String.format("\u5f53\u524d\u503c: [%s] \u4e0d\u5728\u5b57\u6bb5\u8303\u56f4\u5185,\u5b57\u6bb5\u8303\u56f4\u4e3a[%s]", value, Arrays.toString(this.enumList))).addConstraintViolation();
        return false;
    }
}

