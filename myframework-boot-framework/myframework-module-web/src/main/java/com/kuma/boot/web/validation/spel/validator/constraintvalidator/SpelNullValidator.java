/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.validator.constraintvalidator;

import com.kuma.boot.web.validation.spel.core.SpelConstraintValidator;
import com.kuma.boot.web.validation.spel.core.result.FieldValidResult;
import com.kuma.boot.web.validation.spel.validator.constrain.SpelNull;
import java.lang.reflect.Field;

public class SpelNullValidator
implements SpelConstraintValidator<SpelNull> {
    @Override
    public FieldValidResult isValid(SpelNull annotation, Object obj, Field field) throws IllegalAccessException {
        return FieldValidResult.of(field.get(obj) == null);
    }
}

