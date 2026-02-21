package com.kuma.boot.web.validation.spel.validator.constraintvalidator;

import com.kuma.boot.web.validation.spel.core.result.FieldValidResult;
import com.kuma.boot.web.validation.spel.validator.constrain.SpelPast;

import java.lang.reflect.Field;

/**
 * {@link SpelPast} 注解校验器。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2025/07/20
 */
public class SpelPastValidator extends com.kuma.boot.web.validation.spel.validator.constraintvalidator.AbstractSpelTemporalValidator<SpelPast> {

    @Override
    public FieldValidResult isValid( SpelPast annotation, Object obj, Field field) throws IllegalAccessException {
        Object fieldValue = field.get(obj);
        return super.isValid(fieldValue);
    }

    @Override
    protected boolean isValidTemporal(Object temporal) {
        Object now = getNow(temporal);
        return compareTemporal(temporal, now) < 0;
    }
}
