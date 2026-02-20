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
import com.kuma.boot.web.validation.annotation.StringEnums;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Objects;

public class StringEnumsValidator
implements ConstraintValidator<StringEnums, String> {
    private String[] enumList;
    private StringEnums constraintAnnotation;

    public void initialize(StringEnums constraintAnnotation) {
        this.enumList = constraintAnnotation.enumList();
        this.constraintAnnotation = constraintAnnotation;
    }

    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(value) || ArrayUtil.contains((Object[])this.enumList, (Object)value)) {
            return true;
        }
        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate(String.format("\u5f53\u524d\u503c: [%s] \u4e0d\u5728\u5b57\u6bb5\u8303\u56f4\u5185,\u5b57\u6bb5\u5178\u8303\u56f4\u4e3a[%s]", value, Arrays.toString(this.enumList))).addConstraintViolation();
        return false;
    }
}

