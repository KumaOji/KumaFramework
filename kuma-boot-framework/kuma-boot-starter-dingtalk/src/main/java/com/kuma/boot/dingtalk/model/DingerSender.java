/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.model;

import com.kuma.boot.dingtalk.entity.DingerRequest;
import com.kuma.boot.dingtalk.entity.DingerResponse;
import com.kuma.boot.dingtalk.enums.DingerType;
import com.kuma.boot.dingtalk.enums.MessageSubType;

public interface DingerSender {
    public DingerResponse send(MessageSubType var1, DingerRequest var2);

    public DingerResponse send(DingerType var1, MessageSubType var2, DingerRequest var3);
}

