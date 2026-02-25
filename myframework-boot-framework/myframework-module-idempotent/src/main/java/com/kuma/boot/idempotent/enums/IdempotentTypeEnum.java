/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.idempotent.enums;

public enum IdempotentTypeEnum {
    ALL(0, "ALL"),
    RID(1, "RID"),
    KEY(2, "KEY");

    private final Integer index;
    private final String title;

    private IdempotentTypeEnum(Integer index, String title) {
        this.index = index;
        this.title = title;
    }

    public Integer getIndex() {
        return this.index;
    }

    public String getTitle() {
        return this.title;
    }
}

