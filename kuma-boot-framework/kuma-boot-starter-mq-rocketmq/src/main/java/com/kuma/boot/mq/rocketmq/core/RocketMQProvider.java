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

package com.kuma.boot.mq.rocketmq.core;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.mq.common.Message;
import com.kuma.boot.mq.common.MessageQueueProvider;
import com.kuma.boot.mq.common.producer.MessageQueueProducerException;
import com.kuma.boot.mq.common.producer.MessageSendCallback;
import com.kuma.boot.mq.common.producer.MessageSendResult;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;

/** RocketMQ 生产者 */
public class RocketMQProvider implements MessageQueueProvider {

    private static final String ROCKETMQ_PROVIDER_SEND_ERROR = "RocketMQProvider send error: {}";

    private static final long DEFAULT_SEND_TIMEOUT_MILLIS = 3000L;

    private final RocketMQTemplate rocketMQTemplate;

    public RocketMQProvider(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    @Override
    public MessageSendResult syncSend(Message message) throws MessageQueueProducerException {
        try {
            String destination = buildDestination(message);
            org.springframework.messaging.Message<String> msg =
                    MessageBuilder.withPayload(message.getBody()).build();

            SendResult sendResult;
            if (message.getDelayTimeLevel() != null && message.getDelayTimeLevel() > 0) {
                sendResult = rocketMQTemplate.syncSend(
                        destination, msg, DEFAULT_SEND_TIMEOUT_MILLIS, message.getDelayTimeLevel());
            } else {
                sendResult = rocketMQTemplate.syncSend(destination, msg);
            }
            return transfer(sendResult);
        } catch (Exception e) {
            LogUtils.error(ROCKETMQ_PROVIDER_SEND_ERROR, e.getMessage(), e);
            throw new MessageQueueProducerException(e.getMessage());
        }
    }

    @Override
    public void asyncSend(Message message, MessageSendCallback messageCallback) throws MessageQueueProducerException {
        try {
            String destination = buildDestination(message);
            org.springframework.messaging.Message<String> msg =
                    MessageBuilder.withPayload(message.getBody()).build();

            rocketMQTemplate.asyncSend(destination, msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    messageCallback.onSuccess(transfer(sendResult));
                }

                @Override
                public void onException(Throwable e) {
                    messageCallback.onFailed(e);
                }
            });
        } catch (Exception e) {
            LogUtils.error(ROCKETMQ_PROVIDER_SEND_ERROR, e.getMessage(), e);
            throw new MessageQueueProducerException(e.getMessage());
        }
    }

    private String buildDestination(Message message) {
        String topic = message.getTopic();
        if (message.getTags() != null && !message.getTags().isEmpty()) {
            return topic + ":" + message.getTags();
        }
        return topic;
    }

    private MessageSendResult transfer(SendResult sendResult) {
        MessageSendResult result = new MessageSendResult();
        result.setTopic(sendResult.getMessageQueue().getTopic());
        result.setPartition(sendResult.getMessageQueue().getQueueId());
        result.setOffset(sendResult.getQueueOffset());
        result.setTransactionId(sendResult.getTransactionId());
        return result;
    }
}
