/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.lang.Validator
 *  cn.hutool.core.util.CharUtil
 *  cn.hutool.core.util.StrUtil
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  jakarta.validation.ConstraintValidator
 *  jakarta.validation.ConstraintValidatorContext
 */
package com.kuma.boot.web.validation.validator;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.web.validation.annotation.Chinese;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ChineseValidator
implements ConstraintValidator<Chinese, Object> {
    private boolean notNull;

    public void initialize(Chinese constraintAnnotation) {
        this.notNull = constraintAnnotation.notNull();
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        String validValue = null;
        if (value != null && CharUtil.isChar((Object)value) && !CharUtil.isBlankChar((char)((Character)value).charValue()) || value instanceof String && StrUtil.isNotBlank((CharSequence)((String)value))) {
            validValue = StrUtil.toString((Object)value);
        }
        if (StringUtils.isNotBlank(validValue)) {
            return Validator.isChinese(validValue);
        }
        return !this.notNull;
    }
}

