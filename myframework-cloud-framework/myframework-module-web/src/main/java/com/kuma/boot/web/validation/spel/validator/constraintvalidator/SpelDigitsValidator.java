/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.validator.constraintvalidator;

import com.kuma.boot.web.validation.spel.core.SpelConstraintValidator;
import com.kuma.boot.web.validation.spel.core.exception.SpelParserException;
import com.kuma.boot.web.validation.spel.core.parse.SpelParser;
import com.kuma.boot.web.validation.spel.core.result.FieldValidResult;
import com.kuma.boot.web.validation.spel.validator.constrain.SpelDigits;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SpelDigitsValidator
implements SpelConstraintValidator<SpelDigits> {
    static final Set<Class<?>> SUPPORT_TYPE;

    @Override
    public FieldValidResult isValid(SpelDigits annotation, Object obj, Field field) throws IllegalAccessException {
        int actualFractionLength;
        int actualIntegerLength;
        int dotIndex;
        BigDecimal bigDecimalValue;
        String plainString;
        Number numberValue;
        Object fieldValue = field.get(obj);
        if (fieldValue == null) {
            return FieldValidResult.success();
        }
        Number integerValue = SpelParser.parse(annotation.integer(), obj, Number.class);
        int maxIntegerLength = integerValue.intValue();
        if (maxIntegerLength < 0) {
            throw new SpelParserException("Integer length must be non-negative, but got: " + maxIntegerLength);
        }
        Number fractionValue = SpelParser.parse(annotation.fraction(), obj, Number.class);
        int maxFractionLength = fractionValue.intValue();
        if (maxFractionLength < 0) {
            throw new SpelParserException("Fraction length must be non-negative, but got: " + maxFractionLength);
        }
        if (fieldValue instanceof CharSequence) {
            String stringValue = fieldValue.toString();
            try {
                numberValue = new BigDecimal(stringValue);
            }
            catch (NumberFormatException e) {
                return FieldValidResult.of(false, maxIntegerLength, maxFractionLength);
            }
        } else {
            numberValue = (Number)fieldValue;
        }
        if ((plainString = (bigDecimalValue = numberValue instanceof BigDecimal ? (BigDecimal)numberValue : new BigDecimal(numberValue.toString())).stripTrailingZeros().toPlainString()).startsWith("-")) {
            plainString = plainString.substring(1);
        }
        if ((dotIndex = plainString.indexOf(46)) == -1) {
            actualIntegerLength = plainString.length();
            actualFractionLength = 0;
        } else {
            actualIntegerLength = dotIndex;
            actualFractionLength = plainString.length() - dotIndex - 1;
        }
        if (actualIntegerLength == 0 || actualIntegerLength == 1 && plainString.startsWith("0")) {
            actualIntegerLength = 1;
        }
        if (actualIntegerLength > maxIntegerLength || actualFractionLength > maxFractionLength) {
            return FieldValidResult.of(false, maxIntegerLength, maxFractionLength);
        }
        return FieldValidResult.success();
    }

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

