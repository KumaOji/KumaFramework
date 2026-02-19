/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.mq.common.producer;

public interface MessageSendCallback {
    public void onSuccess(MessageSendResult var1);

    public void onFailed(Throwable var1);
}

