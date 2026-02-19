/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.error;

public class ApiFieldError {
    private final String code;
    private final String property;
    private final String message;
    private final Object rejectedValue;

    public ApiFieldError(String code, String property, String message, Object rejectedValue) {
        this.code = code;
        this.property = property;
        this.message = message;
        this.rejectedValue = rejectedValue;
    }

    public String getCode() {
        return this.code;
    }

    public String getProperty() {
        return this.property;
    }

    public String getMessage() {
        return this.message;
    }

    public Object getRejectedValue() {
        return this.rejectedValue;
    }
}

