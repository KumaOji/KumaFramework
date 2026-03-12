//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.model;

public enum CaptchaTypeEnum {
    BLOCKPUZZLE("blockPuzzle", "滑块拼图"),
    CLICKWORD("clickWord", "文字点选"),
    DEFAULT("default", "默认");

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
        for(CaptchaTypeEnum e : values()) {
            if (e.codeValue.equals(codeValue)) {
                return e;
            }
        }

        return null;
    }

    public static String getCodeDescByCodeBalue(String codeValue) {
        CaptchaTypeEnum enumItem = parseFromCodeValue(codeValue);
        return enumItem == null ? "" : enumItem.getCodeDesc();
    }

    public static boolean validateCodeValue(String codeValue) {
        return parseFromCodeValue(codeValue) != null;
    }

    public static String getString() {
        StringBuffer buffer = new StringBuffer();

        for(CaptchaTypeEnum e : values()) {
            buffer.append(e.codeValue).append("--").append(e.getCodeDesc()).append(", ");
        }

        buffer.deleteCharAt(buffer.lastIndexOf(","));
        return buffer.toString().trim();
    }
}
