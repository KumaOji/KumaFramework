/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.model;

public enum CaptchaTypeEnum {
    BLOCKPUZZLE("blockPuzzle", "\u6ed1\u5757\u62fc\u56fe"),
    CLICKWORD("clickWord", "\u6587\u5b57\u70b9\u9009"),
    DEFAULT("default", "\u9ed8\u8ba4");

    private final String codeValue;
    private final String codeDesc;

    private CaptchaTypeEnum(String codeValue, String codeDesc) {
        this.codeValue = codeValue;
        this.codeDesc = codeDesc;
    }

    public String getCodeValue() {
        return this.codeValue;
    }

    public String getCodeDesc() {
        return this.codeDesc;
    }

    public static CaptchaTypeEnum parseFromCodeValue(String codeValue) {
        for (CaptchaTypeEnum e : CaptchaTypeEnum.values()) {
            if (!e.codeValue.equals(codeValue)) continue;
            return e;
        }
        return null;
    }

    public static String getCodeDescByCodeBalue(String codeValue) {
        CaptchaTypeEnum enumItem = CaptchaTypeEnum.parseFromCodeValue(codeValue);
        return enumItem == null ? "" : enumItem.getCodeDesc();
    }

    public static boolean validateCodeValue(String codeValue) {
        return CaptchaTypeEnum.parseFromCodeValue(codeValue) != null;
    }

    public static String getString() {
        StringBuffer buffer = new StringBuffer();
        for (CaptchaTypeEnum e : CaptchaTypeEnum.values()) {
            buffer.append(e.codeValue).append("--").append(e.getCodeDesc()).append(", ");
        }
        buffer.deleteCharAt(buffer.lastIndexOf(","));
        return buffer.toString().trim();
    }
}

