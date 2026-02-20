/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.validator.constraintvalidator;

import com.kuma.boot.web.validation.spel.core.result.FieldValidResult;
import com.kuma.boot.web.validation.spel.validator.constrain.SpelPastOrPresent;

import java.lang.reflect.Field;

public class SpelPastOrPresentValidator
extends AbstractSpelTemporalValidator<SpelPastOrPresent> {
    @Override
    public FieldValidResult isValid(SpelPastOrPresent annotation, Object obj, Field field) throws IllegalAccessException {
        Object fieldValue = field.get(obj);
        return super.isValid(fieldValue);
    }

    @Override
    protected boolean isValidTemporal(Object temporal) {
        Object now = this.getNow(temporal);
        return this.compareTemporal(temporal, now) <= 0;
    }
}

