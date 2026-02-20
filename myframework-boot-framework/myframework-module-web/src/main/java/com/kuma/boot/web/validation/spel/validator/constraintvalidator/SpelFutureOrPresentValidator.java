/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.validator.constraintvalidator;

import com.kuma.boot.web.validation.spel.core.result.FieldValidResult;
import com.kuma.boot.web.validation.spel.validator.constrain.SpelFutureOrPresent;

import java.lang.reflect.Field;

public class SpelFutureOrPresentValidator
extends AbstractSpelTemporalValidator<SpelFutureOrPresent> {
    @Override
    public FieldValidResult isValid(SpelFutureOrPresent annotation, Object obj, Field field) throws IllegalAccessException {
        Object fieldValue = field.get(obj);
        return super.isValid(fieldValue);
    }

    @Override
    protected boolean isValidTemporal(Object temporal) {
        Object now = this.getNow(temporal);
        return this.compareTemporal(temporal, now) >= 0;
    }
}

