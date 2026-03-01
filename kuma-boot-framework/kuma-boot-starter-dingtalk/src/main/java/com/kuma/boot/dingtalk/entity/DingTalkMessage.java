/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

import com.kuma.boot.dingtalk.enums.DingerType;

public class DingTalkMessage
extends MsgType {
    public DingTalkMessage() {
        this.setDingerType(DingerType.DINGTALK);
    }
}

