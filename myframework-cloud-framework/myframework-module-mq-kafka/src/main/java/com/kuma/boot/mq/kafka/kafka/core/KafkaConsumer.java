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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.mq.kafka.kafka.autoconfigure.KafkaMessageQueueAutoConfiguration;
import com.kuma.boot.mq.common.Message;
import com.kuma.boot.mq.common.MessageQueueConsumer;
import com.kuma.boot.mq.common.MessageQueueListener;
import com.kuma.boot.mq.common.MessageQueueProperties;
import org.apache.commons.collections4.CollectionUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.core.ConsumerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/** RocketMQ 消费者 */
public class KafkaConsumer implements InitializingBean, DisposableBean {

    private static final String KAFKA_CONSUMER_PROCESSOR_CONSUME_ERROR = "KafkaConsumerProcessor consume error: {}";

    public static final String INITIALIZING_KAFKA_CONSUMER = "Initializing KafkaConsumer";

    public static final String DESTROY_KAFKA_CONSUMER = "Destroy KafkaConsumer";

    public static final String CREATE_CONSUMER_FROM_CONSUMER_FACTORY_GROUP_TOPIC =
            "Create consumer from consumerFactory, group: {}, topic: {}";

    private final List<Consumer<String, String>> consumers = Lists.newArrayList();

    private final MessageQueueProperties messageQueueProperties;

    private final KafkaProperties kafkaProperties;

    private final List<MessageQueueConsumer> messageQueueConsumers;

    private final ConsumerFactory<String, String> consumerFactory;

    private final TaskExecutor taskExecutor;

    public KafkaConsumer(
            MessageQueueProperties messageQueueProperties,
            KafkaProperties kafkaProperties,
            List<MessageQueueConsumer> messageQueueConsumers,
            ConsumerFactory<String, String> consumerFactory,
            TaskExecutor taskExecutor) {
        this.messageQueueProperties = messageQueueProperties;
        this.kafkaProperties = kafkaProperties;
        this.messageQueueConsumers = messageQueueConsumers;
        this.consumerFactory = consumerFactory;
        this.taskExecutor = taskExecutor;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.debug(INITIALIZING_KAFKA_CONSUMER);
        if (CollectionUtils.isEmpty(messageQueueConsumers)) {
            return;
        }
        for (MessageQueueConsumer messageQueueConsumer : messageQueueConsumers) {
            Consumer<String, String> consumer = createConsumer(messageQueueConsumer);
            if (consumer == null) {
                continue;
            }
            consumers.add(consumer);
            taskExecutor.execute(() -> {
                while (true) {
                    try {
                        ConsumerRecords<String, String> consumerRecords =
                                consumer.poll(kafkaProperties.getConsumer().getFetchMaxWait());
                        if (consumerRecords == null || consumerRecords.isEmpty()) {
                            continue;
                        }
                        int maxPollRecords = kafkaProperties.getConsumer().getMaxPollRecords();
                        Map<TopicPartition, OffsetAndMetadata> offsets =
                                Maps.newHashMapWithExpectedSize(maxPollRecords);
                        List<Message> messages = Lists.newArrayListWithCapacity(consumerRecords.count());
                        consumerRecords.forEach(record -> {
                            offsets.put(
                                    new TopicPartition(record.topic(), record.partition()),
                                    new OffsetAndMetadata(record.offset() + 1));

                            Message message = new Message();
                            message.setTopic(record.topic());
                            message.setPartition(record.partition());
                            message.setKey(record.key());
                            message.setBody(record.value());
                            messages.add(message);
                        });
                        messageQueueConsumer.consume(messages, () -> consumer.commitSync(offsets));
                    } catch (Exception e) {
                        LogUtils.error(KAFKA_CONSUMER_PROCESSOR_CONSUME_ERROR, e.getMessage(), e);
                    }
                }
            });
        }
    }

    @Override
    public void destroy() {
        LogUtils.debug(DESTROY_KAFKA_CONSUMER);
        consumers.forEach(Consumer::unsubscribe);
    }

    private Consumer<String, String> createConsumer(MessageQueueConsumer messageQueueConsumer) {
        Class<? extends MessageQueueConsumer> clazz = messageQueueConsumer.getClass();
        MessageQueueListener annotation = clazz.getAnnotation(MessageQueueListener.class);

        if (StringUtils.isNotBlank(annotation.type())
                && !messageQueueProperties.getType().equalsIgnoreCase(annotation.type())) {
            return null;
        }

        if (StringUtils.isBlank(annotation.type())
                && !KafkaMessageQueueAutoConfiguration.TYPE.equalsIgnoreCase(messageQueueProperties.getType())) {
            return null;
        }

        String topic = annotation.topic();

        String group = null;
        if (StringUtils.isNotBlank(annotation.group())) {
            group = annotation.group();
        } else if (StringUtils.isNotBlank(kafkaProperties.getConsumer().getGroupId())) {
            group = kafkaProperties.getConsumer().getGroupId() + "_" + topic;
        }

        Consumer<String, String> consumer = consumerFactory.createConsumer(group, kafkaProperties.getClientId());
        consumer.subscribe(Collections.singleton(topic));

        LogUtils.debug(CREATE_CONSUMER_FROM_CONSUMER_FACTORY_GROUP_TOPIC, group, topic);
        return consumer;
    }
}
