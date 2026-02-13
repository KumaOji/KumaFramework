/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  jakarta.validation.ConstraintViolation
 *  jakarta.validation.Validation
 *  jakarta.validation.Validator
 */
package com.kuma.boot.common.utils.validator;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.enums.ValidatorExceptionEnum;
import com.kuma.boot.common.exception.CheckException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Iterator;
import java.util.Set;

public class ValidatorUtils {
    private static final Validator VALIDATOR_INSTANCE = Validation.buildDefaultValidatorFactory().getValidator();

    public static <T> Set<ConstraintViolation<T>> validate(T object, Class<?> ... groups) {
        return VALIDATOR_INSTANCE.validate(object, (Class[])groups);
    }

    public static boolean simpleValidate(Object object, Class<?> ... groups) {
        Set constraintViolations = VALIDATOR_INSTANCE.validate(object, (Class[])groups);
        return constraintViolations.isEmpty();
    }

    public static void validateThrowMessage(Object object, Class<?> ... groups) {
        String errorMessage = ValidatorUtils.validateGetMessage(object, groups);
        if (errorMessage != null) {
            throw new CheckException(ValidatorExceptionEnum.VALIDATED_RESULT_ERROR.getDesc());
        }
    }

    public static String validateGetMessage(Object object, Class<?> ... groups) {
        Set constraintViolations = VALIDATOR_INSTANCE.validate(object, (Class[])groups);
        if (!constraintViolations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            Iterator it = constraintViolations.iterator();
            while (it.hasNext()) {
                ConstraintViolation violation = (ConstraintViolation)it.next();
                errorMessage.append(violation.getMessage());
                if (!it.hasNext()) continue;
                errorMessage.append(", ");
            }
            return StrUtil.format((CharSequence)ValidatorExceptionEnum.VALIDATED_RESULT_ERROR.getUserTip(), (Object[])new Object[0]);
        }
        return null;
    }
}

