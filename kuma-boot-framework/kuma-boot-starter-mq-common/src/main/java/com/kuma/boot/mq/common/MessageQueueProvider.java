package com.kuma.boot.mq.common;

import com.kuma.boot.mq.common.producer.MessageQueueProducerException;
import com.kuma.boot.mq.common.producer.MessageSendCallback;
import com.kuma.boot.mq.common.producer.MessageSendResult;

public interface MessageQueueProvider {
    MessageSendResult syncSend(Message message) throws MessageQueueProducerException;

    void asyncSend(Message message, MessageSendCallback callback) throws MessageQueueProducerException;
}
