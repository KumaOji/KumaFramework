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

/**
 * {@link com.kuma.boot.web.validation.spel.jakarta.SpelValid} 的实际校验器
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/4/11
 */
public class SpelValidator implements ConstraintValidator<com.kuma.boot.web.validation.spel.jakarta.SpelValid, Object> {

    private com.kuma.boot.web.validation.spel.jakarta.SpelValid spelValid;

    @Override
    public void initialize( com.kuma.boot.web.validation.spel.jakarta.SpelValid constraintAnnotation) {
        this.spelValid = constraintAnnotation;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        // 表达式不为空且计算结果为 false，跳过校验
        if (!spelValid.condition().isEmpty() && !SpelParser.parse(spelValid.condition(), value, Boolean.class)) {
            LogUtils.debug("SpelValid condition is not satisfied, skip validation, condition: {}", spelValid.condition());
            return true;
        }

        // 构建上下文
        SpelValidContext spelValidContext = SpelValidContext.builder()
                .locale(LocaleContextHolder.getLocale())
                .build();

        // 校验对象
        ObjectValidResult validateObjectResult = SpelValidExecutor.validateObject(value, spelValid.spelGroups(), spelValidContext);

        // 构建错误信息
        buildConstraintViolation(validateObjectResult, context);
        return validateObjectResult.noneError();
    }

    /**
     * 生成错误信息并将其添加到验证上下文
     */
    private void buildConstraintViolation( ObjectValidResult validateObjectResult, ConstraintValidatorContext context) {
        if (validateObjectResult.noneError()) {
            return;
        }
        context.disableDefaultConstraintViolation();
        for (FieldError error : validateObjectResult.getErrors()) {
            context.buildConstraintViolationWithTemplate(error.getErrorMessage()).addPropertyNode(error.getFieldName()).addConstraintViolation();
        }
    }

}
