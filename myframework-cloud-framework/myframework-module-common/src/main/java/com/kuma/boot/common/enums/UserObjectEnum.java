/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.enums;

import com.kuma.boot.common.enums.base.CommonEnum;

public enum UserObjectEnum implements CommonEnum
{
    DEPT(1, "dept", "\u90e8\u95e8"),
    POSITION(2, "position", "\u5c97\u4f4d"),
    ROLE(3, "role", "\u89d2\u8272"),
    ORG(4, "org", "\u7ec4\u7ec7"),
    MANAGER(5, "dataScope", "\u6570\u636e\u6743\u9650");

    private final int code;
    private final String value;
    private final String desc;

    private UserObjectEnum(int code, String value, String desc) {
        this.code = code;
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return this.value;
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
        for (UserObjectEnum result : UserObjectEnum.values()) {
            if (result.getCode() != code) continue;
            return result.name().toLowerCase();
        }
        return null;
    }
}

