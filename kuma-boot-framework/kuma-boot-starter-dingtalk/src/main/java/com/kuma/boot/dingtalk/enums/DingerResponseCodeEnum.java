/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.enums;

public enum DingerResponseCodeEnum {
    SUCCESS("D000", "success"),
    DINGER_DISABLED("D101", "Dinger\u672a\u542f\u7528"),
    MESSAGE_TYPE_UNSUPPORTED("D201", "\u65e0\u6cd5\u652f\u6301\u7684\u6d88\u606f\u7c7b\u578b"),
    SEND_MESSAGE_FAILED("D202", "\u6d88\u606f\u53d1\u9001\u5931\u8d25"),
    MESSAGE_PROCESSING_FAILED("D203", "\u6d88\u606f\u5904\u7406\u5f02\u5e38"),
    FAILED("D999", "failed");

    private String code;
    private String message;

    private DingerResponseCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }
}

