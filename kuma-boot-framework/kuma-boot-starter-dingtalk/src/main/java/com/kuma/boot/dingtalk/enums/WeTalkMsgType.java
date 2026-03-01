/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.enums;

public enum WeTalkMsgType {
    TEXT("text"),
    MARKDOWN("markdown"),
    IMAGE("image"),
    NEWS("news"),
    FILE("file");

    private String type;

    private WeTalkMsgType(String type) {
        this.type = type;
    }

    public String type() {
        return this.type;
    }
}

