/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.enums;

public enum GlobalStatusEnum {
    ENABLE(0, "\u5f00\u542f"),
    DISABLE(1, "\u5173\u95ed");

    private final Integer value;
    private final String label;

    private GlobalStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getLabel() {
        return this.label;
    }
}

