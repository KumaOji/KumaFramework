/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.mq.common;

import com.kuma.boot.mq.common.consumer.Acknowledgement;
import java.util.List;

public interface MessageQueueConsumer {
    public void consume(List<Message> var1, Acknowledgement var2);
}

