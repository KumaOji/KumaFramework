/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.enums;

import com.kuma.boot.common.enums.base.CommonEnum;

public enum MemberTypeEnum implements CommonEnum
{
    PERSONAL(1, "personal", "\u4e2a\u4eba\u7528\u6237"),
    ENTERPRISE(2, "enterprise", "\u4f01\u4e1a\u7528\u6237");

    private final int code;
    private final String value;
    private final String desc;

    private MemberTypeEnum(int code, String value, String desc) {
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
        for (MemberTypeEnum result : MemberTypeEnum.values()) {
            if (result.getCode() != code) continue;
            return result.name().toLowerCase();
        }
        return null;
    }
}

