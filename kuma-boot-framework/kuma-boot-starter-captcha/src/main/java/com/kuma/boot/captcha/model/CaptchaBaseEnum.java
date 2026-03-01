/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.model;

public enum CaptchaBaseEnum {
    ORIGINAL("ORIGINAL", "\u6ed1\u52a8\u62fc\u56fe\u5e95\u56fe"),
    SLIDING_BLOCK("SLIDING_BLOCK", "\u6ed1\u52a8\u62fc\u56fe\u6ed1\u5757\u5e95\u56fe"),
    PIC_CLICK("PIC_CLICK", "\u6587\u5b57\u70b9\u9009\u5e95\u56fe");

    private final String codeValue;
    private final String codeDesc;

    private CaptchaBaseEnum(String codeValue, String codeDesc) {
        this.codeValue = codeValue;
        this.codeDesc = codeDesc;
    }

    public String getCodeValue() {
        return this.codeValue;
    }

    public String getCodeDesc() {
        return this.codeDesc;
    }

    public static CaptchaBaseEnum parseFromCodeValue(String codeValue) {
        for (CaptchaBaseEnum e : CaptchaBaseEnum.values()) {
            if (!e.codeValue.equals(codeValue)) continue;
            return e;
        }
        return null;
    }

    public static String getCodeDescByCodeBalue(String codeValue) {
        CaptchaBaseEnum enumItem = CaptchaBaseEnum.parseFromCodeValue(codeValue);
        return enumItem == null ? "" : enumItem.getCodeDesc();
    }

    public static boolean validateCodeValue(String codeValue) {
        return CaptchaBaseEnum.parseFromCodeValue(codeValue) != null;
    }

    public static String getString() {
        StringBuffer buffer = new StringBuffer();
        for (CaptchaBaseEnum e : CaptchaBaseEnum.values()) {
            buffer.append(e.codeValue).append("--").append(e.getCodeDesc()).append(", ");
        }
        buffer.deleteCharAt(buffer.lastIndexOf(","));
        return buffer.toString().trim();
    }
}

