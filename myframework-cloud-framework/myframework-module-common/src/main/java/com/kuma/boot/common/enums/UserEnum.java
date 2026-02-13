/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.enums;

import com.kuma.boot.common.enums.base.CommonEnum;

public enum UserEnum implements CommonEnum
{
    MEMBER(1, "\u4f1a\u5458"),
    STORE(2, "\u5546\u5bb6"),
    MANAGER(3, "\u7ba1\u7406\u5458"),
    SYSTEM(4, "\u7cfb\u7edf");

    private final int code;
    private final String desc;

    private UserEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    public String getNameByCode(int code) {
        for (UserEnum result : UserEnum.values()) {
            if (result.getCode() != code) continue;
            return result.name().toLowerCase();
        }
        return null;
    }

    public static String getByCode(int code) {
        for (UserEnum result : UserEnum.values()) {
            if (result.getCode() != code) continue;
            return result.name().toLowerCase();
        }
        return null;
    }

    public static UserEnum getEnumByCode(int code) {
        for (UserEnum result : UserEnum.values()) {
            if (result.getCode() != code) continue;
            return result;
        }
        return null;
    }
}

