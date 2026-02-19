/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.error;

public class ApiParameterError {
    private final String code;
    private final String parameter;
    private final String message;
    private final Object rejectedValue;

    public ApiParameterError(String code, String parameter, String message, Object rejectedValue) {
        this.code = code;
        this.parameter = parameter;
        this.message = message;
        this.rejectedValue = rejectedValue;
    }

    public String getCode() {
        return this.code;
    }

    public String getParameter() {
        return this.parameter;
    }

    public String getMessage() {
        return this.message;
    }

    public Object getRejectedValue() {
        return this.rejectedValue;
    }
}

