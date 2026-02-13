/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.enums;

import com.kuma.boot.common.enums.base.CommonEnum;

public enum SexEnum implements CommonEnum
{
    UNKNOWN(0, "UN", "\u672a\u77e5"),
    MALE(1, "M", "\u7537"),
    FEMALE(2, "F", "\u5973");

    private final int code;
    private final String desc;
    private final String value;

    private SexEnum(int code, String value, String desc) {
        this.code = code;
        this.desc = desc;
        this.value = value;
    }

    public String getNameByCode(int code) {
        for (SexEnum result : SexEnum.values()) {
            if (result.getCode() != code) continue;
            return result.name().toLowerCase();
        }
        return null;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    public String getValue() {
        return this.value;
    }
}

