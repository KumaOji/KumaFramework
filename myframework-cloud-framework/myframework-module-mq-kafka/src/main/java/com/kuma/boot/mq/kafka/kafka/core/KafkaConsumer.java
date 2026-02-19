/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.mq.common.Message
 *  com.kuma.boot.mq.common.MessageQueueConsumer
 *  com.kuma.boot.mq.common.MessageQueueListener
 *  com.kuma.boot.mq.common.MessageQueueProperties
 *  org.apache.commons.collections4.CollectionUtils
 *  org.apache.kafka.clients.consumer.Consumer
 *  org.apache.kafka.clients.consumer.ConsumerRecords
 *  org.apache.kafka.clients.consumer.OffsetAndMetadata
 *  org.apache.kafka.common.TopicPartition
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.kafka.autoconfigure.KafkaProperties
 *  org.springframework.core.task.TaskExecutor
 *  org.springframework.kafka.core.ConsumerFactory
 */
package com.kuma.boot.mq.kafka.kafka.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.mq.common.Message;
import com.kuma.boot.mq.common.MessageQueueConsumer;
import com.kuma.boot.mq.common.MessageQueueListener;
import com.kuma.boot.mq.common.MessageQueueProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.core.ConsumerFactory;

public class KafkaConsumer
implements InitializingBean,
DisposableBean {
    private static final String KAFKA_CONSUMER_PROCESSOR_CONSUME_ERROR = "KafkaConsumerProcessor consume error: {}";
    public static final String INITIALIZING_KAFKA_CONSUMER = "Initializing KafkaConsumer";
    public static final String DESTROY_KAFKA_CONSUMER = "Destroy KafkaConsumer";
    public static final String CREATE_CONSUMER_FROM_CONSUMER_FACTORY_GROUP_TOPIC = "Create consumer from consumerFactory, group: {}, topic: {}";
    private final List<Consumer<String, String>> consumers = Lists.newArrayList();
    private final MessageQueueProperties messageQueueProperties;
    private final KafkaProperties kafkaProperties;
    private final List<MessageQueueConsumer> messageQueueConsumers;
    private final ConsumerFactory<String, String> consumerFactory;
    private final TaskExecutor taskExecutor;

    public KafkaConsumer(MessageQueueProperties messageQueueProperties, KafkaProperties kafkaProperties, List<MessageQueueConsumer> messageQueueConsumers, ConsumerFactory<String, String> consumerFactory, TaskExecutor taskExecutor) {
        this.messageQueueProperties = messageQueueProperties;
        this.kafkaProperties = kafkaProperties;
        this.messageQueueConsumers = messageQueueConsumers;
        this.consumerFactory = consumerFactory;
        this.taskExecutor = taskExecutor;
    }

    public void afterPropertiesSet() throws Exception {
        LogUtils.debug((String)INITIALIZING_KAFKA_CONSUMER, (Object[])new Object[0]);
        if (CollectionUtils.isEmpty(this.messageQueueConsumers)) {
            return;
        }
        for (MessageQueueConsumer messageQueueConsumer : this.messageQueueConsumers) {
            Consumer<String, String> consumer = this.createConsumer(messageQueueConsumer);
            if (consumer == null) continue;
            this.consumers.add(consumer);
            this.taskExecutor.execute(() -> {
                while (true) {
                    try {
                        while (true) {
                            ConsumerRecords consumerRecords;
                            if ((consumerRecords = consumer.poll(this.kafkaProperties.getConsumer().getFetchMaxWait())) == null || consumerRecords.isEmpty()) {
                                continue;
                            }
                            int maxPollRecords = this.kafkaProperties.getConsumer().getMaxPollRecords();
                            HashMap offsets = Maps.newHashMapWithExpectedSize((int)maxPollRecords);
                            ArrayList messages = Lists.newArrayListWithCapacity((int)consumerRecords.count());
                            consumerRecords.forEach(record -> {
                                offsets.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1L));
                                Message message = new Message();
                                message.setTopic(record.topic());
                                message.setPartition(Integer.valueOf(record.partition()));
                                message.setKey((String)record.key());
                                message.setBody((String)record.value());
                                messages.add(message);
                            });
                            messageQueueConsumer.consume((List)messages, () -> consumer.commitSync(offsets));
                        }
                    }
                    catch (Exception e) {
                        LogUtils.error((String)KAFKA_CONSUMER_PROCESSOR_CONSUME_ERROR, (Object[])new Object[]{e.getMessage(), e});
                        continue;
                    }
                    break;
                }
            });
        }
    }

    public void destroy() {
        LogUtils.debug((String)DESTROY_KAFKA_CONSUMER, (Object[])new Object[0]);
        this.consumers.forEach(Consumer::unsubscribe);
    }

    private Consumer<String, String> createConsumer(MessageQueueConsumer messageQueueConsumer) {
        Class clazz = messageQueueConsumer.getClass();
        MessageQueueListener annotation = clazz.getAnnotation(MessageQueueListener.class);
        if (StringUtils.isNotBlank((String)annotation.type()) && !this.messageQueueProperties.getType().equalsIgnoreCase(annotation.type())) {
            return null;
        }
        if (StringUtils.isBlank((String)annotation.type()) && !"KAFKA".equalsIgnoreCase(this.messageQueueProperties.getType())) {
            return null;
        }
        String topic = annotation.topic();
        Object group = null;
        if (StringUtils.isNotBlank((String)annotation.group())) {
            group = annotation.group();
        } else if (StringUtils.isNotBlank((String)this.kafkaProperties.getConsumer().getGroupId())) {
            group = this.kafkaProperties.getConsumer().getGroupId() + "_" + topic;
        }
        Consumer consumer = this.consumerFactory.createConsumer((String)group, this.kafkaProperties.getClientId());
        consumer.subscribe(Collections.singleton(topic));
        LogUtils.debug((String)CREATE_CONSUMER_FROM_CONSUMER_FACTORY_GROUP_TOPIC, (Object[])new Object[]{group, topic});
        return consumer;
    }
}

