package com.kuma.boot.web.validation.spel.validator.constraintvalidator;

import com.kuma.boot.web.validation.spel.core.SpelConstraintValidator;
import com.kuma.boot.web.validation.spel.core.result.FieldValidResult;
import com.kuma.boot.web.validation.spel.core.util.CalcLengthUtil;
import com.kuma.boot.web.validation.spel.validator.constrain.SpelNotEmpty;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * {@link SpelNotEmpty} 注解校验器。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/5/5
 */
public class SpelNotEmptyValidator implements SpelConstraintValidator<SpelNotEmpty> {

    @Override
    public FieldValidResult isValid( SpelNotEmpty annotation, Object obj, Field field) throws IllegalAccessException {
        Object object = field.get(obj);
        if (object == null) {
            return FieldValidResult.of(false);
        }
        int size = CalcLengthUtil.calcFieldSize(object);
        return FieldValidResult.of(size > 0);
    }

    @Override
    public Set<Class<?>> supportType() {
        return CalcLengthUtil.SUPPORT_TYPE;
    }

}
