/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.model;

import com.kuma.boot.dingtalk.autoconfigure.properties.DingtalkProperties;
import com.kuma.boot.dingtalk.entity.DingerCallback;
import com.kuma.boot.dingtalk.enums.MessageSubType;
import com.kuma.boot.dingtalk.exception.DingerException;
import com.kuma.boot.dingtalk.support.CustomMessage;

public abstract class AbstractDingerSender
extends DingerHelper
implements DingerSender {
    protected DingtalkProperties dingtalkProperties;
    protected DingerManagerBuilder dingTalkManagerBuilder;

    public AbstractDingerSender(DingtalkProperties dingtalkProperties, DingerManagerBuilder dingTalkManagerBuilder) {
        this.dingtalkProperties = dingtalkProperties;
        this.dingTalkManagerBuilder = dingTalkManagerBuilder;
    }

    protected CustomMessage customMessage(MessageSubType messageSubType) {
        return messageSubType == MessageSubType.TEXT ? this.dingTalkManagerBuilder.getTextMessage() : this.dingTalkManagerBuilder.getMarkDownMessage();
    }

    protected <T> void exceptionCallback(String dingerId, T message, DingerException ex) {
        DingerCallback<T> dkExCallable = new DingerCallback<T>(dingerId, message, ex);
        this.dingTalkManagerBuilder.getDingerExceptionCallback().execute(dkExCallable);
    }
}

