/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.enums;

import com.kuma.boot.common.enums.base.CommonEnum;

public enum ClientTypeEnum implements CommonEnum
{
    H5(1, "\u79fb\u52a8\u7aef"),
    PC(2, "PC\u7aef"),
    WECHAT_MP(3, "\u5c0f\u7a0b\u5e8f\u7aef"),
    APP(4, "\u79fb\u52a8\u5e94\u7528\u7aef"),
    UNKNOWN(5, "\u672a\u77e5");

    private final int code;
    private final String clientName;

    private ClientTypeEnum(int code, String des) {
        this.code = code;
        this.clientName = des;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getDesc() {
        return this.clientName;
    }
}

