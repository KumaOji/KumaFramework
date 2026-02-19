/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.exception.enums;

public enum ExceptionHandleTypeEnum {
    LOGGER("\u4e0d\u901a\u77e5"),
    DING_TALK("\u901a\u8fc7\u9489\u9489\u901a\u77e5"),
    MAIL("\u90ae\u4ef6\u901a\u77e5");

    private final String text;

    public String getText() {
        return this.text;
    }

    private ExceptionHandleTypeEnum(String text) {
        this.text = text;
    }
}

