//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.model;

public enum CaptchaBaseEnum {
    ORIGINAL("ORIGINAL", "滑动拼图底图"),
    SLIDING_BLOCK("SLIDING_BLOCK", "滑动拼图滑块底图"),
    PIC_CLICK("PIC_CLICK", "文字点选底图");

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
        for(CaptchaBaseEnum e : values()) {
            if (e.codeValue.equals(codeValue)) {
                return e;
            }
        }

        return null;
    }

    public static String getCodeDescByCodeBalue(String codeValue) {
        CaptchaBaseEnum enumItem = parseFromCodeValue(codeValue);
        return enumItem == null ? "" : enumItem.getCodeDesc();
    }

    public static boolean validateCodeValue(String codeValue) {
        return parseFromCodeValue(codeValue) != null;
    }

    public static String getString() {
        StringBuffer buffer = new StringBuffer();

        for(CaptchaBaseEnum e : values()) {
            buffer.append(e.codeValue).append("--").append(e.getCodeDesc()).append(", ");
        }

        buffer.deleteCharAt(buffer.lastIndexOf(","));
        return buffer.toString().trim();
    }
}
