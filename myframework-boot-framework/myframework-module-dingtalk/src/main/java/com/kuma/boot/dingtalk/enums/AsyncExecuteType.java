/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.enums;

public enum AsyncExecuteType {
    TRUE(true),
    FALSE(false),
    NONE(false);

    private boolean type;

    private AsyncExecuteType(boolean type) {
        this.type = type;
    }

    public boolean type() {
        return this.type;
    }
}

