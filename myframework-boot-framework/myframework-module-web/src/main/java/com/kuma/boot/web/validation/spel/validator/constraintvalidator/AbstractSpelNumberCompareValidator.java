/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.validator.constraintvalidator;

import com.kuma.boot.web.validation.spel.core.SpelConstraintValidator;
import com.kuma.boot.web.validation.spel.core.exception.SpelParserException;
import com.kuma.boot.web.validation.spel.core.parse.SpelParser;
import com.kuma.boot.web.validation.spel.core.result.FieldValidResult;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractSpelNumberCompareValidator<T extends Annotation>
implements SpelConstraintValidator<T> {
    static final Set<Class<?>> SUPPORT_TYPE;

    public FieldValidResult isValid(T anno, Object fieldValue, String spel, Object obj) {
        Object numberValue;
        Number numberFieldValue;
        if (fieldValue == null) {
            return FieldValidResult.success();
        }
        if (fieldValue instanceof CharSequence) {
            String stringValue = fieldValue.toString();
            try {
                numberFieldValue = new BigDecimal(stringValue);
            }
            catch (NumberFormatException e) {
                return FieldValidResult.of(false, fieldValue);
            }
        } else {
            numberFieldValue = (Number)fieldValue;
        }
        if (!((numberValue = SpelParser.parse(spel, obj)) instanceof Number)) {
            throw new SpelParserException("Expression [" + spel + "] calculate result must be Number.");
        }
        if (!this.compare(anno, numberFieldValue, (Number)numberValue)) {
            return FieldValidResult.of(false, numberValue);
        }
        return FieldValidResult.success();
    }

    protected abstract boolean compare(T var1, Number var2, Number var3);

    @Override
    public Set<Class<?>> supportType() {
        return SUPPORT_TYPE;
    }

    static {
        HashSet<Class<Object>> hashSet = new HashSet<Class<Object>>();
        hashSet.add(Number.class);
        hashSet.add(Integer.TYPE);
        hashSet.add(Long.TYPE);
        hashSet.add(Float.TYPE);
        hashSet.add(Double.TYPE);
        hashSet.add(Short.TYPE);
        hashSet.add(Byte.TYPE);
        hashSet.add(CharSequence.class);
        SUPPORT_TYPE = Collections.unmodifiableSet(hashSet);
    }
}

