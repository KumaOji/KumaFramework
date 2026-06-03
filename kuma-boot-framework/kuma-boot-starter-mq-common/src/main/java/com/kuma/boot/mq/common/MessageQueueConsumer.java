package com.kuma.boot.mq.common;

import com.kuma.boot.mq.common.consumer.Acknowledgement;

import java.util.List;

public interface MessageQueueConsumer {
    void consume(List<Message> messages, Acknowledgement acknowledgement);
}
