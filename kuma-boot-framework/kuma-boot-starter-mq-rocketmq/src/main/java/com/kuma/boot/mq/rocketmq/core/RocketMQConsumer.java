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

import com.google.common.collect.Lists;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.mq.common.Message;
import com.kuma.boot.mq.common.MessageQueueConsumer;
import com.kuma.boot.mq.common.MessageQueueListener;
import com.kuma.boot.mq.common.MessageQueueProperties;
import com.kuma.boot.mq.rocketmq.autoconfigure.RocketMQMessageQueueAutoConfiguration;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;

/** RocketMQ 消费者 */
public class RocketMQConsumer implements InitializingBean, DisposableBean {

    private static final String ROCKETMQ_CONSUMER_CONSUME_ERROR = "RocketMQConsumer consume error: {}";

    public static final String INITIALIZING_ROCKETMQ_CONSUMER = "Initializing RocketMQConsumer";

    public static final String DESTROY_ROCKETMQ_CONSUMER = "Destroy RocketMQConsumer";

    public static final String CREATE_CONSUMER_GROUP_TOPIC =
            "Create RocketMQ consumer, group: {}, topic: {}";

    private final List<DefaultMQPushConsumer> consumers = Lists.newArrayList();

    private final MessageQueueProperties messageQueueProperties;

    private final RocketMQProperties rocketMQProperties;

    private final List<MessageQueueConsumer> messageQueueConsumers;

    public RocketMQConsumer(
            MessageQueueProperties messageQueueProperties,
            RocketMQProperties rocketMQProperties,
            List<MessageQueueConsumer> messageQueueConsumers) {
        this.messageQueueProperties = messageQueueProperties;
        this.rocketMQProperties = rocketMQProperties;
        this.messageQueueConsumers = messageQueueConsumers;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.debug(INITIALIZING_ROCKETMQ_CONSUMER);
        if (CollectionUtils.isEmpty(messageQueueConsumers)) {
            return;
        }
        for (MessageQueueConsumer messageQueueConsumer : messageQueueConsumers) {
            DefaultMQPushConsumer consumer = createConsumer(messageQueueConsumer);
            if (consumer == null) {
                continue;
            }
            consumers.add(consumer);
        }
    }

    @Override
    public void destroy() {
        LogUtils.debug(DESTROY_ROCKETMQ_CONSUMER);
        consumers.forEach(DefaultMQPushConsumer::shutdown);
    }

    private DefaultMQPushConsumer createConsumer(MessageQueueConsumer messageQueueConsumer) throws Exception {
        Class<? extends MessageQueueConsumer> clazz = messageQueueConsumer.getClass();
        MessageQueueListener annotation = clazz.getAnnotation(MessageQueueListener.class);
        if (annotation == null) {
            return null;
        }

        if (StringUtils.isNotBlank(annotation.type())
                && !RocketMQMessageQueueAutoConfiguration.TYPE.equalsIgnoreCase(annotation.type())) {
            return null;
        }

        if (StringUtils.isBlank(annotation.type())
                && !RocketMQMessageQueueAutoConfiguration.TYPE.equalsIgnoreCase(
                        messageQueueProperties.getType())) {
            return null;
        }

        String topic = annotation.topic();
        String selectorExpression =
                StringUtils.isNotBlank(annotation.selectorExpression()) ? annotation.selectorExpression() : "*";

        String group = StringUtils.isNotBlank(annotation.group())
                ? annotation.group()
                : rocketMQProperties.getProducer().getGroup() + "_" + topic;

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(group);
        consumer.setNamesrvAddr(rocketMQProperties.getNameServer());

        if ("SQL92".equalsIgnoreCase(annotation.selectorType())) {
            consumer.subscribe(topic, MessageSelector.bySql(selectorExpression));
        } else {
            consumer.subscribe(topic, selectorExpression);
        }

        if (annotation.consumeMessageBatchMaxSize() > 0) {
            consumer.setConsumeMessageBatchMaxSize(annotation.consumeMessageBatchMaxSize());
        }

        if (annotation.pullBatchSize() > 0) {
            consumer.setPullBatchSize(annotation.pullBatchSize());
        }

        if ("BROADCASTING".equalsIgnoreCase(annotation.messageModel())) {
            consumer.setMessageModel(MessageModel.BROADCASTING);
        }

        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            try {
                List<Message> messages = new ArrayList<>(msgs.size());
                for (MessageExt msg : msgs) {
                    Message message = new Message();
                    message.setTopic(msg.getTopic());
                    message.setTags(msg.getTags());
                    message.setKey(msg.getKeys());
                    message.setBody(new String(msg.getBody()));
                    messages.add(message);
                }
                messageQueueConsumer.consume(messages, () -> {});
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            } catch (Exception e) {
                LogUtils.error(ROCKETMQ_CONSUMER_CONSUME_ERROR, e.getMessage(), e);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });

        LogUtils.debug(CREATE_CONSUMER_GROUP_TOPIC, group, topic);
        consumer.start();
        return consumer;
    }
}
