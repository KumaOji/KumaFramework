package com.kuma.boot.web.validation.spel.validator.constraintvalidator;

import com.kuma.boot.web.validation.spel.core.SpelConstraintValidator;
import com.kuma.boot.web.validation.spel.core.result.FieldValidResult;
import com.kuma.boot.web.validation.spel.validator.constrain.SpelNull;

import java.lang.reflect.Field;

/**
 * {@link SpelNull} 注解校验器。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/5/5
 */
public class SpelNullValidator implements SpelConstraintValidator<SpelNull> {

    @Override
    public FieldValidResult isValid( SpelNull annotation, Object obj, Field field) throws IllegalAccessException {
        return FieldValidResult.of(field.get(obj) == null);
    }

}
