/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.mq.kafka.kafka.core;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.mq.common.Message;
import com.kuma.boot.mq.common.MessageQueueProvider;
import com.kuma.boot.mq.common.producer.MessageQueueProducerException;
import com.kuma.boot.mq.common.producer.MessageSendCallback;
import com.kuma.boot.mq.common.producer.MessageSendResult;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

/** Kafka 生产者 */
public class KafkaProvider implements MessageQueueProvider {

    private static final String KAFKA_PROVIDER_SEND_INTERRUPTED = "KafkaProvider send interrupted: {}";

    private static final String KAFKA_PROVIDER_CONSUME_ERROR = "KafkaProvider send error: {}";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProvider(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 同步发送消息
     *
     * @param message
     * @return
     * @throws MessageQueueProducerException
     */
    @Override
    public MessageSendResult syncSend(Message message) throws MessageQueueProducerException {
        try {
            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(message.getTopic(), message.getBody());
            SendResult<String, String> sendResult = future.get();
            return transfer(sendResult);
        } catch (InterruptedException e) {
            LogUtils.error(KAFKA_PROVIDER_SEND_INTERRUPTED, e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new MessageQueueProducerException(e.getMessage());
        } catch (Exception e) {
            LogUtils.error(KAFKA_PROVIDER_CONSUME_ERROR, e.getMessage(), e);
            throw new MessageQueueProducerException(e.getMessage());
        }
    }

    /**
     * 异步发送消息
     *
     * @param message
     * @param messageCallback
     * @throws MessageQueueProducerException
     */
    @Override
    public void asyncSend(Message message, MessageSendCallback messageCallback) throws MessageQueueProducerException {
        try {
            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(message.getTopic(), message.getBody());
            // future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            //
            //	@Override
            //	public void onSuccess(SendResult<String, String> sendResult) {
            //		messageCallback.onSuccess(transfer(sendResult));
            //	}
            //
            //	@Override
            //	public void onFailure(Throwable e) {
            //		messageCallback.onFailed(e);
            //	}
            // });
        } catch (Exception e) {
            LogUtils.error(KAFKA_PROVIDER_CONSUME_ERROR, e.getMessage(), e);
            throw new MessageQueueProducerException(e.getMessage());
        }
    }

    /**
     * 转化为自定义的 MessageSendResult
     *
     * @param sendResult
     * @return
     */
    private MessageSendResult transfer(SendResult<String, String> sendResult) {
        ProducerRecord<String, String> producerRecord = sendResult.getProducerRecord();
        RecordMetadata recordMetadata = sendResult.getRecordMetadata();
        MessageSendResult result = new MessageSendResult();
        result.setTopic(producerRecord.topic());
        result.setPartition(recordMetadata.partition());
        result.setOffset(recordMetadata.offset());
        return result;
    }
}
