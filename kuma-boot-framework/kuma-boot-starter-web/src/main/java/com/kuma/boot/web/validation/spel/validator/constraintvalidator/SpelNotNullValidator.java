package com.kuma.boot.web.validation.spel.validator.constraintvalidator;

import com.kuma.boot.web.validation.spel.core.SpelConstraintValidator;
import com.kuma.boot.web.validation.spel.core.result.FieldValidResult;
import com.kuma.boot.web.validation.spel.validator.constrain.SpelNotNull;

import java.lang.reflect.Field;

/**
 * {@link SpelNotNull} 注解校验器。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/5/1
 */
public class SpelNotNullValidator implements SpelConstraintValidator<SpelNotNull> {

    @Override
    public FieldValidResult isValid( SpelNotNull annotation, Object obj, Field field) throws IllegalAccessException {
        return FieldValidResult.of(field.get(obj) != null);
    }

}
