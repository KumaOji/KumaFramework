/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.enums;

import com.kuma.boot.common.enums.base.CommonEnum;

public enum LockFlagEnum implements CommonEnum
{
    LOCKED(0, "\u9501\u5b9a"),
    NORMAL(1, "\u6b63\u5e38");

    private final int code;
    private final String desc;

    private LockFlagEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getNameByCode(int code) {
        for (LockFlagEnum result : LockFlagEnum.values()) {
            if (result.getCode() != code) continue;
            return result.name().toLowerCase();
        }
        return null;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }
}

