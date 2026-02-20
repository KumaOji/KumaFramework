/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.validator.constraintvalidator;

import com.kuma.boot.web.validation.spel.core.result.FieldValidResult;
import com.kuma.boot.web.validation.spel.core.util.NumberComparatorUtil;
import com.kuma.boot.web.validation.spel.validator.constrain.SpelMin;

import java.lang.reflect.Field;

public class SpelMinValidator
extends AbstractSpelNumberCompareValidator<SpelMin> {
    @Override
    protected boolean compare(SpelMin anno, Number fieldValue, Number compareValue) {
        int compareResult = NumberComparatorUtil.compare(fieldValue, compareValue, NumberComparatorUtil.LESS_THAN);
        return anno.inclusive() ? compareResult >= 0 : compareResult > 0;
    }

    @Override
    public FieldValidResult isValid(SpelMin annotation, Object obj, Field field) throws IllegalAccessException {
        return super.isValid(annotation, field.get(obj), annotation.value(), obj);
    }
}

