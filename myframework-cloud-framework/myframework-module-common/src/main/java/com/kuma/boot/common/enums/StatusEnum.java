/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.enums;

public enum StatusEnum {
    SUCCESS("\u6210\u529f"),
    FAILURE("\u5931\u8d25"),
    PROCESSING("\u5904\u7406\u4e2d"),
    UNKNOWN("\u72b6\u6001\u672a\u77e5\u6216\u5e42\u7b49\u51b2\u7a81\u6216\u91cd\u8bd5\u6216\u91cd\u590d\u8bf7\u6c42\u6216\u672a\u77e5\u5f02\u5e38");

    private final String description;

    private StatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}

