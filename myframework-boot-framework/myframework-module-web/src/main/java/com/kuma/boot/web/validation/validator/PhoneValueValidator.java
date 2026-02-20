/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.lang.PatternPool
 *  cn.hutool.core.util.ReUtil
 *  cn.hutool.core.util.StrUtil
 *  jakarta.validation.ConstraintValidator
 *  jakarta.validation.ConstraintValidatorContext
 */
package com.kuma.boot.web.validation.validator;

import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.web.validation.annotation.PhoneValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneValueValidator
implements ConstraintValidator<PhoneValue, String> {
    private Boolean required;

    public void initialize(PhoneValue constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }

    public boolean isValid(String phoneValue, ConstraintValidatorContext context) {
        if (StrUtil.isEmpty((CharSequence)phoneValue)) {
            return this.required == false;
        }
        return ReUtil.isMatch((Pattern)PatternPool.MOBILE, (CharSequence)phoneValue);
    }
}

