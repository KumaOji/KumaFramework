/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.enums;

import com.kuma.boot.common.enums.base.CommonEnum;

public enum SwitchEnum implements CommonEnum
{
    OPEN(1, "\u5f00\u542f"),
    CLOSE(2, "\u5173\u95ed");

    private final int code;
    private final String description;

    private SwitchEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getDesc() {
        return this.description;
    }
}

