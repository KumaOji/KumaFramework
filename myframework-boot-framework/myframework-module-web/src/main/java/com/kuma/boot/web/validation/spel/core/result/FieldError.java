/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.core.result;

public class FieldError {
    private final String fieldName;
    private final String errorMessage;

    public String toString() {
        return "FieldError{fieldName='" + this.fieldName + "', errorMessage='" + this.errorMessage + "'}";
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public FieldError(String fieldName, String errorMessage) {
        this.fieldName = fieldName;
        this.errorMessage = errorMessage;
    }

    public static FieldError of(String fieldName, String errorMessage) {
        return new FieldError(fieldName, errorMessage);
    }
}

