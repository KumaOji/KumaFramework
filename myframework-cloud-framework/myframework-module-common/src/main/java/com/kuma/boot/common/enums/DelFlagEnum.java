/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.enums;

import com.kuma.boot.common.enums.base.CommonEnum;

public enum DelFlagEnum implements CommonEnum
{
    DELETE(0, "\u5220\u9664", Boolean.TRUE),
    NORMAL(1, "\u6b63\u5e38", Boolean.FALSE);

    private final int code;
    private final String desc;
    private final Boolean delFlag;

    private DelFlagEnum(int code, String desc, Boolean delFlag) {
        this.code = code;
        this.desc = desc;
        this.delFlag = delFlag;
    }

    public String getNameByCode(int code) {
        for (DelFlagEnum result : DelFlagEnum.values()) {
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

    public Boolean getDelFlag() {
        return this.delFlag;
    }

    public Boolean delFlag() {
        return this.delFlag;
    }
}

