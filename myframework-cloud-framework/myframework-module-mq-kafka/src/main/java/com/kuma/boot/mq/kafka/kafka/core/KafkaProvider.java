/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.mq.common.Message
 *  com.kuma.boot.mq.common.MessageQueueProvider
 *  com.kuma.boot.mq.common.producer.MessageQueueProducerException
 *  com.kuma.boot.mq.common.producer.MessageSendCallback
 *  com.kuma.boot.mq.common.producer.MessageSendResult
 *  org.apache.kafka.clients.producer.ProducerRecord
 *  org.apache.kafka.clients.producer.RecordMetadata
 *  org.springframework.kafka.core.KafkaTemplate
 *  org.springframework.kafka.support.SendResult
 */
package com.kuma.boot.mq.kafka.kafka.core;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.mq.common.Message;
import com.kuma.boot.mq.common.MessageQueueProvider;
import com.kuma.boot.mq.common.producer.MessageQueueProducerException;
import com.kuma.boot.mq.common.producer.MessageSendCallback;
import com.kuma.boot.mq.common.producer.MessageSendResult;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

public class KafkaProvider
implements MessageQueueProvider {
    private static final String KAFKA_PROVIDER_SEND_INTERRUPTED = "KafkaProvider send interrupted: {}";
    private static final String KAFKA_PROVIDER_CONSUME_ERROR = "KafkaProvider send error: {}";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProvider(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public MessageSendResult syncSend(Message message) throws MessageQueueProducerException {
        try {
            CompletableFuture future = this.kafkaTemplate.send(message.getTopic(), (Object)message.getBody());
            SendResult sendResult = (SendResult)future.get();
            return this.transfer((SendResult<String, String>)sendResult);
        }
        catch (InterruptedException e) {
            LogUtils.error((String)KAFKA_PROVIDER_SEND_INTERRUPTED, (Object[])new Object[]{e.getMessage(), e});
            Thread.currentThread().interrupt();
            throw new MessageQueueProducerException(e.getMessage());
        }
        catch (Exception e) {
            LogUtils.error((String)KAFKA_PROVIDER_CONSUME_ERROR, (Object[])new Object[]{e.getMessage(), e});
            throw new MessageQueueProducerException(e.getMessage());
        }
    }

    public void asyncSend(Message message, MessageSendCallback messageCallback) throws MessageQueueProducerException {
        try {
            CompletableFuture completableFuture = this.kafkaTemplate.send(message.getTopic(), (Object)message.getBody());
        }
        catch (Exception e) {
            LogUtils.error((String)KAFKA_PROVIDER_CONSUME_ERROR, (Object[])new Object[]{e.getMessage(), e});
            throw new MessageQueueProducerException(e.getMessage());
        }
    }

    private MessageSendResult transfer(SendResult<String, String> sendResult) {
        ProducerRecord producerRecord = sendResult.getProducerRecord();
        RecordMetadata recordMetadata = sendResult.getRecordMetadata();
        MessageSendResult result = new MessageSendResult();
        result.setTopic(producerRecord.topic());
        result.setPartition(Integer.valueOf(recordMetadata.partition()));
        result.setOffset(Long.valueOf(recordMetadata.offset()));
        return result;
    }
}

