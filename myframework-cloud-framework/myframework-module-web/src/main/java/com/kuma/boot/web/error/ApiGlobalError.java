/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.error;

public class ApiGlobalError {
    private final String code;
    private final String message;

    public ApiGlobalError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}

