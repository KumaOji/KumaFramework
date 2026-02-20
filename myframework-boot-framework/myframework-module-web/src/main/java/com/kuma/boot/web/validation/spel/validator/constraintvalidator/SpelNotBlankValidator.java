/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.web.validation.spel.validator.constraintvalidator;

import com.kuma.boot.web.validation.spel.core.SpelConstraintValidator;
import com.kuma.boot.web.validation.spel.core.result.FieldValidResult;
import com.kuma.boot.web.validation.spel.validator.constrain.SpelNotBlank;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;
import org.springframework.util.StringUtils;

public class SpelNotBlankValidator
implements SpelConstraintValidator<SpelNotBlank> {
    private static final Set<Class<?>> SUPPORT_TYPE = Collections.singleton(CharSequence.class);

    @Override
    public FieldValidResult isValid(SpelNotBlank annotation, Object obj, Field field) throws IllegalAccessException {
        CharSequence fieldValue = (CharSequence)field.get(obj);
        return FieldValidResult.of(StringUtils.hasText((CharSequence)fieldValue));
    }

    @Override
    public Set<Class<?>> supportType() {
        return SUPPORT_TYPE;
    }
}

