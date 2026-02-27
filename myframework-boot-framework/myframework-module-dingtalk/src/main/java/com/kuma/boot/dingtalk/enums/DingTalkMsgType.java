/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.enums;

public enum DingTalkMsgType {
    TEXT("text"),
    LINK("link"),
    MARKDOWN("markdown"),
    ACTION_CARD("actionCard"),
    FEED_CARD("feedCard");

    private String type;

    private DingTalkMsgType(String type) {
        this.type = type;
    }

    public String type() {
        return this.type;
    }
}

