/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.validator.constraintvalidator;

import com.kuma.boot.web.validation.spel.core.SpelConstraintValidator;
import com.kuma.boot.web.validation.spel.core.parse.SpelParser;
import com.kuma.boot.web.validation.spel.core.result.FieldValidResult;
import com.kuma.boot.web.validation.spel.core.util.CalcLengthUtil;
import com.kuma.boot.web.validation.spel.validator.constrain.SpelSize;
import java.lang.reflect.Field;
import java.util.Set;

public class SpelSizeValidator
implements SpelConstraintValidator<SpelSize> {
    @Override
    public FieldValidResult isValid(SpelSize annotation, Object obj, Field field) throws IllegalAccessException {
        Object fieldValue = field.get(obj);
        if (fieldValue == null) {
            return FieldValidResult.success();
        }
        int size = CalcLengthUtil.calcFieldSize(fieldValue);
        Integer min = SpelParser.parse(annotation.min(), obj, Integer.class);
        Integer max = SpelParser.parse(annotation.max(), obj, Integer.class);
        if (size < min || size > max) {
            return FieldValidResult.of(false, min, max);
        }
        return FieldValidResult.success();
    }

    @Override
    public Set<Class<?>> supportType() {
        return CalcLengthUtil.SUPPORT_TYPE;
    }
}

