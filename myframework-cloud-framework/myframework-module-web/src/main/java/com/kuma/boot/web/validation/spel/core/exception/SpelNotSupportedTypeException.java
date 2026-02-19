/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.core.exception;

import java.util.Set;

public class SpelNotSupportedTypeException
extends SpelValidatorException {
    private final Class<?> clazz;
    private final Set<Class<?>> supperType;

    public Class<?> getClazz() {
        return this.clazz;
    }

    public Set<Class<?>> getSupperType() {
        return this.supperType;
    }

    public SpelNotSupportedTypeException(Class<?> clazz, Set<Class<?>> supperType) {
        super("Class type not supported, current type: " + clazz.getName() + ", supper type: " + supperType.toString());
        this.clazz = clazz;
        this.supperType = supperType;
    }
}

