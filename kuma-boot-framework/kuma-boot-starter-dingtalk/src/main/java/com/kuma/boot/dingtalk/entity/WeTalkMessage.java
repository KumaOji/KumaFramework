/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

import com.kuma.boot.dingtalk.enums.DingerType;

public class WeTalkMessage
extends MsgType {
    public WeTalkMessage() {
        this.setDingerType(DingerType.WETALK);
    }
}

