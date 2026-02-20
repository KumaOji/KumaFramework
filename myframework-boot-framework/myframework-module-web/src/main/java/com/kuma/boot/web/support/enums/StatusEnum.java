/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.support.enums;

public enum StatusEnum {
    ENABLE("enable", "\u542f\u7528"),
    DISABLE("disable", "\u7981\u7528");

    private final String code;
    private final String message;

    private StatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static StatusEnum codeToEnum(String code) {
        if (null != code) {
            for (StatusEnum e : StatusEnum.values()) {
                if (!e.getCode().equals(code)) continue;
                return e;
            }
        }
        return null;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}

