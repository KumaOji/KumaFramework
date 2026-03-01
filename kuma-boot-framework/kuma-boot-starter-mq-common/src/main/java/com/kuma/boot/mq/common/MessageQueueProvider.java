/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.mq.common;

import com.kuma.boot.mq.common.producer.MessageQueueProducerException;
import com.kuma.boot.mq.common.producer.MessageSendCallback;
import com.kuma.boot.mq.common.producer.MessageSendResult;

public interface MessageQueueProvider {
    public MessageSendResult syncSend(Message var1) throws MessageQueueProducerException;

    public void asyncSend(Message var1, MessageSendCallback var2) throws MessageQueueProducerException;
}

