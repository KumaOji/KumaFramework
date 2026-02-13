/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.enums;

import com.kuma.boot.common.enums.base.CommonEnum;

public enum UserTypeEnum implements CommonEnum
{
    MEMBER(1, "member", "\u4f1a\u5458\u7528\u6237"),
    MANAGER(2, "backend", "\u5e73\u53f0\u540e\u53f0\u7ba1\u7406\u7528\u6237");

    private final int code;
    private final String value;
    private final String desc;

    private UserTypeEnum(int code, String value, String desc) {
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
        for (UserTypeEnum result : UserTypeEnum.values()) {
            if (result.getCode() != code) continue;
            return result.name().toLowerCase();
        }
        return null;
    }
}

