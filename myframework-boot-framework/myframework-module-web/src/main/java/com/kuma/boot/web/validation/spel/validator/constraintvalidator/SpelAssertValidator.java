/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.validator.constraintvalidator;

import com.kuma.boot.web.validation.spel.core.SpelConstraintValidator;
import com.kuma.boot.web.validation.spel.core.exception.SpelArgumentException;
import com.kuma.boot.web.validation.spel.core.parse.SpelParser;
import com.kuma.boot.web.validation.spel.core.result.FieldValidResult;
import com.kuma.boot.web.validation.spel.validator.constrain.SpelAssert;
import java.lang.reflect.Field;

public class SpelAssertValidator
implements SpelConstraintValidator<SpelAssert> {
    @Override
    public FieldValidResult isValid(SpelAssert annotation, Object obj, Field field) {
        if (annotation.assertTrue().isEmpty()) {
            throw new SpelArgumentException("assertTrue must not be empty");
        }
        return FieldValidResult.of(SpelParser.parse(annotation.assertTrue(), obj, Boolean.class));
    }
}

