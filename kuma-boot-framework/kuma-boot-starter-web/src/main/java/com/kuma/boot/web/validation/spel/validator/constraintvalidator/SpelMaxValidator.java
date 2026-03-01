package com.kuma.boot.web.validation.spel.validator.constraintvalidator;

import com.kuma.boot.web.validation.spel.core.result.FieldValidResult;
import com.kuma.boot.web.validation.spel.core.util.NumberComparatorUtil;
import com.kuma.boot.web.validation.spel.validator.constrain.SpelMax;

import java.lang.reflect.Field;

/**
 * {@link SpelMax} 注解校验器。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/9/29
 */
public class SpelMaxValidator extends com.kuma.boot.web.validation.spel.validator.constraintvalidator.AbstractSpelNumberCompareValidator<SpelMax> {

    @Override
    protected boolean compare( SpelMax anno, Number fieldValue, Number compareValue) {
        int compareResult = NumberComparatorUtil.compare(fieldValue, compareValue, NumberComparatorUtil.GREATER_THAN);
        return anno.inclusive() ? compareResult <= 0 : compareResult < 0;
    }

    @Override
    public FieldValidResult isValid( SpelMax annotation, Object obj, Field field) throws IllegalAccessException {
        return super.isValid(annotation, field.get(obj), annotation.value(), obj);
    }

}
