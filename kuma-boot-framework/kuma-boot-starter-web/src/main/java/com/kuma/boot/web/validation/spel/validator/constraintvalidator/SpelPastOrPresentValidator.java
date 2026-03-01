package com.kuma.boot.web.validation.spel.validator.constraintvalidator;

import com.kuma.boot.web.validation.spel.core.result.FieldValidResult;
import com.kuma.boot.web.validation.spel.validator.constrain.SpelPastOrPresent;

import java.lang.reflect.Field;

/**
 * {@link SpelPastOrPresent} 注解校验器。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2025/07/20
 */
public class SpelPastOrPresentValidator extends com.kuma.boot.web.validation.spel.validator.constraintvalidator.AbstractSpelTemporalValidator<SpelPastOrPresent> {

    @Override
    public FieldValidResult isValid( SpelPastOrPresent annotation, Object obj, Field field) throws IllegalAccessException {
        Object fieldValue = field.get(obj);
        return super.isValid(fieldValue);
    }

    @Override
    protected boolean isValidTemporal(Object temporal) {
        Object now = getNow(temporal);
        return compareTemporal(temporal, now) <= 0;
    }
}
