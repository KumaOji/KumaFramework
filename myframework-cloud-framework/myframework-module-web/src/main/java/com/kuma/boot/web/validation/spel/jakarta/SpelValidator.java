/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  jakarta.validation.ConstraintValidator
 *  jakarta.validation.ConstraintValidatorContext
 *  org.springframework.context.i18n.LocaleContextHolder
 */
package com.kuma.boot.web.validation.spel.jakarta;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.validation.spel.core.SpelValidContext;
import com.kuma.boot.web.validation.spel.core.SpelValidExecutor;
import com.kuma.boot.web.validation.spel.core.parse.SpelParser;
import com.kuma.boot.web.validation.spel.core.result.FieldError;
import com.kuma.boot.web.validation.spel.core.result.ObjectValidResult;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.context.i18n.LocaleContextHolder;

public class SpelValidator
implements ConstraintValidator<SpelValid, Object> {
    private SpelValid spelValid;

    public void initialize(SpelValid constraintAnnotation) {
        this.spelValid = constraintAnnotation;
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (!this.spelValid.condition().isEmpty() && !SpelParser.parse(this.spelValid.condition(), value, Boolean.class).booleanValue()) {
            LogUtils.debug((String)"SpelValid condition is not satisfied, skip validation, condition: {}", (Object[])new Object[]{this.spelValid.condition()});
            return true;
        }
        SpelValidContext spelValidContext = SpelValidContext.builder().locale(LocaleContextHolder.getLocale()).build();
        ObjectValidResult validateObjectResult = SpelValidExecutor.validateObject(value, this.spelValid.spelGroups(), spelValidContext);
        this.buildConstraintViolation(validateObjectResult, context);
        return validateObjectResult.noneError();
    }

    private void buildConstraintViolation(ObjectValidResult validateObjectResult, ConstraintValidatorContext context) {
        if (validateObjectResult.noneError()) {
            return;
        }
        context.disableDefaultConstraintViolation();
        for (FieldError error : validateObjectResult.getErrors()) {
            context.buildConstraintViolationWithTemplate(error.getErrorMessage()).addPropertyNode(error.getFieldName()).addConstraintViolation();
        }
    }
}

