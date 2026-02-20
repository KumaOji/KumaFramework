/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.util.ReUtil
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  jakarta.validation.ConstraintValidator
 *  jakarta.validation.ConstraintValidatorContext
 */
package com.kuma.boot.web.validation.validator;

import cn.hutool.core.util.ReUtil;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.web.validation.annotation.CheckMobileValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CheckMobileValidator
implements ConstraintValidator<CheckMobileValid, String> {
    public static final String MOBILE_REG = "[1]\\d{10}";

    public void initialize(CheckMobileValid isMobile) {
    }

    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return StringUtils.isBlank((String)s) || ReUtil.isMatch((String)MOBILE_REG, (CharSequence)s);
    }
}

