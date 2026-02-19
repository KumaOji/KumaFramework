/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.core;

import com.kuma.boot.web.validation.spel.core.result.FieldValidResult;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;

public interface SpelConstraintValidator<A extends Annotation> {
    public static final Set<Class<?>> DEFAULT_SUPPORT_TYPE = Collections.singleton(Object.class);

    public FieldValidResult isValid(A var1, Object var2, Field var3) throws IllegalAccessException;

    default public Set<Class<?>> supportType() {
        return DEFAULT_SUPPORT_TYPE;
    }
}

