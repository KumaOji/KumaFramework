/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.support.encryption.api.enums;

public enum EncryptTypeEnum {
    ADDRESS("ADDRESS", "\u5730\u5740"),
    BANK_CARD_NUM("BANK_CARD_NUM", "\u94f6\u884c\u5361\u53f7"),
    EMAIL("EMAIL", "\u90ae\u7bb1"),
    ID_CARD("ID_CARD", "\u8eab\u4efd\u8bc1"),
    NAME("NAME", "\u59d3\u540d"),
    PASSWORD("PASSWORD", "\u5bc6\u7801"),
    PHONE("PHONE", "\u59d3\u540d");

    private final String code;
    private final String desc;

    private EncryptTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
}

