/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.support.enums;

public enum YesOrNotEnum {
    Y("Y", "\u662f"),
    N("N", "\u5426");

    private final String code;
    private final String message;

    private YesOrNotEnum(String code, String message) {
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

