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

package com.kuma.boot.mq.rabbitmq.core;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.mq.common.Message;
import com.kuma.boot.mq.common.MessageQueueProvider;
import com.kuma.boot.mq.common.producer.MessageQueueProducerException;
import com.kuma.boot.mq.common.producer.MessageSendCallback;
import com.kuma.boot.mq.common.producer.MessageSendResult;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.concurrent.CompletableFuture;

/** RabbitMQ 消息生产者 */
public class RabbitMQProvider implements MessageQueueProvider {

    private static final String RABBITMQ_PROVIDER_SEND_ERROR = "RabbitMQProvider send error: {}";

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQProvider(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 同步发送消息。
     * exchange = message.topic，routingKey = message.key（为空时回退为 topic）。
     * 当 delayTimeLevel > 0 时，设置消息级别 TTL（毫秒），消息过期后由死信交换机路由至延迟目标队列。
     */
    @Override
    public MessageSendResult syncSend(Message message) throws MessageQueueProducerException {
        try {
            String exchange = message.getTopic() != null ? message.getTopic() : "";
            String routingKey = (message.getKey() != null && !message.getKey().isEmpty())
                    ? message.getKey()
                    : exchange;

            if (message.getDelayTimeLevel() != null && message.getDelayTimeLevel() > 0) {
                String expiration = String.valueOf((long) message.getDelayTimeLevel() * 1000);
                rabbitTemplate.convertAndSend(exchange, routingKey, message.getBody(), msg -> {
                    msg.getMessageProperties().setExpiration(expiration);
                    return msg;
                });
            } else {
                rabbitTemplate.convertAndSend(exchange, routingKey, message.getBody());
            }

            MessageSendResult result = new MessageSendResult();
            result.setTopic(message.getTopic());
            return result;
        } catch (Exception e) {
            LogUtils.error(RABBITMQ_PROVIDER_SEND_ERROR, e.getMessage(), e);
            throw new MessageQueueProducerException(e.getMessage(), e);
        }
    }

    /**
     * 异步发送消息，通过 CompletableFuture 在公共线程池中执行，完成后回调 callback。
     */
    @Override
    public void asyncSend(Message message, MessageSendCallback callback) throws MessageQueueProducerException {
        CompletableFuture.supplyAsync(() -> {
            try {
                return syncSend(message);
            } catch (MessageQueueProducerException e) {
                throw new RuntimeException(e);
            }
        }).whenComplete((result, ex) -> {
            if (ex != null) {
                Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                LogUtils.error(RABBITMQ_PROVIDER_SEND_ERROR, cause.getMessage(), cause);
                callback.onFailed(cause);
            } else {
                callback.onSuccess(result);
            }
        });
    }
}
