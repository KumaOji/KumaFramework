/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.enums;

public enum DingerType {
    DINGTALK("\u9489\u9489", "https://oapi.dingtalk.com/robot/send?access_token", true),
    WETALK("\u4f01\u4e1a\u5fae\u4fe1", "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key", true);

    private final String type;
    private final String robotUrl;
    private final boolean enabled;

    private DingerType(String type, String robotUrl, boolean enabled) {
        this.type = type;
        this.robotUrl = robotUrl;
        this.enabled = enabled;
    }

    public String getType() {
        return this.type;
    }

    public String getRobotUrl() {
        return this.robotUrl;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}

