/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  jakarta.validation.ConstraintValidator
 *  jakarta.validation.ConstraintValidatorContext
 */
package com.kuma.boot.web.validation.validator;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.web.support.enums.YesOrNotEnum;
import com.kuma.boot.web.validation.annotation.FlagValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FlagValueValidator
implements ConstraintValidator<FlagValue, String> {
    private Boolean required;

    public void initialize(FlagValue constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }

    public boolean isValid(String flagValue, ConstraintValidatorContext context) {
        if (this.required.booleanValue()) {
            return YesOrNotEnum.Y.getCode().equals(flagValue) || YesOrNotEnum.N.getCode().equals(flagValue);
        }
        if (StrUtil.isEmpty((CharSequence)flagValue)) {
            return true;
        }
        return YesOrNotEnum.Y.getCode().equals(flagValue) || YesOrNotEnum.N.getCode().equals(flagValue);
    }
}

