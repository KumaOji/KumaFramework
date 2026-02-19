/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.validator.constraintvalidator;

import com.kuma.boot.web.validation.spel.core.SpelConstraintValidator;
import com.kuma.boot.web.validation.spel.core.result.FieldValidResult;
import com.kuma.boot.web.validation.spel.validator.constrain.SpelNotNull;
import java.lang.reflect.Field;

public class SpelNotNullValidator
implements SpelConstraintValidator<SpelNotNull> {
    @Override
    public FieldValidResult isValid(SpelNotNull annotation, Object obj, Field field) throws IllegalAccessException {
        return FieldValidResult.of(field.get(obj) != null);
    }
}

