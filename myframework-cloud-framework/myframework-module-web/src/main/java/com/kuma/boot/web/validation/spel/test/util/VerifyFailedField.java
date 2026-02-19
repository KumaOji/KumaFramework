/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.test.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VerifyFailedField {
    String name;
    String message;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private VerifyFailedField() {
    }

    public static VerifyFailedField of(String fieldName) {
        return VerifyFailedField.of(fieldName, null);
    }

    public static <T> VerifyFailedField of(IGetter<T, ?> field) {
        return VerifyFailedField.of(field, null);
    }

    @SafeVarargs
    public static <T> List<VerifyFailedField> of(IGetter<T, ?> ... fields) {
        return Arrays.stream(fields).map(VerifyFailedField::of).collect(Collectors.toList());
    }

    public static <T> VerifyFailedField of(IGetter<T, ?> field, String errorMessage) {
        return VerifyFailedField.of(BeanUtil.getFieldName(field), errorMessage);
    }

    public static VerifyFailedField of(String fieldName, String errorMessage) {
        VerifyFailedField verifyFailedField = new VerifyFailedField();
        verifyFailedField.setName(fieldName);
        verifyFailedField.setMessage(errorMessage);
        return verifyFailedField;
    }
}

