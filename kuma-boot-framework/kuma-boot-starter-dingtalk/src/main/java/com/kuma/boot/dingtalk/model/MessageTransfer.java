/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.model;

import com.kuma.boot.dingtalk.entity.MsgType;

import java.util.Map;

public interface MessageTransfer {
    public MsgType transfer(DingerDefinition var1, Map<String, Object> var2);
}

