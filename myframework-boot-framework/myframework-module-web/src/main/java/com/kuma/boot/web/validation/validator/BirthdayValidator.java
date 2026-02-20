/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.date.DateUtil
 *  cn.hutool.core.lang.Validator
 *  com.kuma.boot.common.utils.date.DateUtils
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  jakarta.validation.ConstraintValidator
 *  jakarta.validation.ConstraintValidatorContext
 */
package com.kuma.boot.web.validation.validator;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Validator;
import com.kuma.boot.common.utils.date.DateUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.web.validation.annotation.Birthday;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

public class BirthdayValidator
implements ConstraintValidator<Birthday, Object> {
    private boolean notNull;

    public void initialize(Birthday constraintAnnotation) {
        this.notNull = constraintAnnotation.notNull();
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        String validValue = null;
        if (value instanceof String) {
            validValue = (String)value;
        } else if (value instanceof Date) {
            validValue = DateUtil.formatDate((Date)((Date)value));
        } else if (value instanceof TemporalAccessor) {
            validValue = DateUtils.toDateFormatter((TemporalAccessor)((TemporalAccessor)value));
        }
        if (StringUtils.isNotBlank((String)validValue)) {
            return Validator.isBirthday((CharSequence)validValue);
        }
        return !this.notNull;
    }
}

