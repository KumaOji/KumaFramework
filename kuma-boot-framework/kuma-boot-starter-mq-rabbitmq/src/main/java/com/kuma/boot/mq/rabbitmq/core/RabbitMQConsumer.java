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

import com.google.common.collect.Lists;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.mq.common.Message;
import com.kuma.boot.mq.common.MessageQueueConsumer;
import com.kuma.boot.mq.common.MessageQueueListener;
import com.kuma.boot.mq.common.MessageQueueProperties;
import com.kuma.boot.mq.rabbitmq.autoconfigure.RabbitMQMessageQueueAutoConfiguration;
import com.kuma.boot.mq.rabbitmq.autoconfigure.properties.RabbitMQProperties;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/** RabbitMQ 消费者：为每个 {@link MessageQueueConsumer} 创建独立的监听容器 */
public class RabbitMQConsumer implements InitializingBean, DisposableBean {

    private static final String INITIALIZING_RABBITMQ_CONSUMER = "Initializing RabbitMQConsumer";

    private static final String DESTROY_RABBITMQ_CONSUMER = "Destroy RabbitMQConsumer";

    private static final String CREATE_CONTAINER_QUEUE = "Create RabbitMQ listener container, queue: {}";

    private static final String RABBITMQ_CONSUMER_ACK_ERROR = "RabbitMQ basicAck failed, queue: {}, error: {}";

    private static final String RABBITMQ_CONSUMER_NACK_ERROR = "RabbitMQ basicNack failed, queue: {}, error: {}";

    private static final String RABBITMQ_CONSUMER_PROCESS_ERROR = "RabbitMQ consumer process error, queue: {}, error: {}";

    private final List<SimpleMessageListenerContainer> containers = Lists.newArrayList();

    private final MessageQueueProperties messageQueueProperties;

    private final RabbitMQProperties rabbitMQProperties;

    private final List<MessageQueueConsumer> messageQueueConsumers;

    private final ConnectionFactory connectionFactory;

    private final RabbitAdmin rabbitAdmin;

    public RabbitMQConsumer(
            MessageQueueProperties messageQueueProperties,
            RabbitMQProperties rabbitMQProperties,
            List<MessageQueueConsumer> messageQueueConsumers,
            ConnectionFactory connectionFactory,
            RabbitAdmin rabbitAdmin) {
        this.messageQueueProperties = messageQueueProperties;
        this.rabbitMQProperties = rabbitMQProperties;
        this.messageQueueConsumers = messageQueueConsumers;
        this.connectionFactory = connectionFactory;
        this.rabbitAdmin = rabbitAdmin;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.debug(INITIALIZING_RABBITMQ_CONSUMER);
        if (CollectionUtils.isEmpty(messageQueueConsumers)) {
            return;
        }
        for (MessageQueueConsumer consumer : messageQueueConsumers) {
            SimpleMessageListenerContainer container = createContainer(consumer);
            if (container == null) {
                continue;
            }
            containers.add(container);
            container.afterPropertiesSet();
            container.start();
        }
    }

    @Override
    public void destroy() {
        LogUtils.debug(DESTROY_RABBITMQ_CONSUMER);
        containers.forEach(SimpleMessageListenerContainer::stop);
    }

    private SimpleMessageListenerContainer createContainer(MessageQueueConsumer messageQueueConsumer) {
        Class<? extends MessageQueueConsumer> clazz = messageQueueConsumer.getClass();
        MessageQueueListener annotation = clazz.getAnnotation(MessageQueueListener.class);
        if (annotation == null) {
            return null;
        }

        if (StringUtils.isNotBlank(annotation.type())
                && !RabbitMQMessageQueueAutoConfiguration.TYPE.equalsIgnoreCase(annotation.type())) {
            return null;
        }
        if (StringUtils.isBlank(annotation.type())
                && !RabbitMQMessageQueueAutoConfiguration.TYPE.equalsIgnoreCase(
                        messageQueueProperties.getType())) {
            return null;
        }

        String queueName = annotation.topic();

        if (rabbitMQProperties.isAutoDeclare()) {
            declareInfrastructure(queueName);
        }

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames(queueName);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setConcurrentConsumers(rabbitMQProperties.getConcurrentConsumers());
        container.setMaxConcurrentConsumers(rabbitMQProperties.getMaxConcurrentConsumers());

        int prefetch = annotation.pullBatchSize() > 0
                ? annotation.pullBatchSize()
                : rabbitMQProperties.getPrefetchCount();
        container.setPrefetchCount(prefetch);

        container.setMessageListener((ChannelAwareMessageListener) (amqpMessage, channel) -> {
            long deliveryTag = amqpMessage.getMessageProperties().getDeliveryTag();
            try {
                Message message = convertMessage(amqpMessage);
                messageQueueConsumer.consume(
                        Collections.singletonList(message),
                        () -> {
                            try {
                                channel.basicAck(deliveryTag, false);
                            } catch (IOException e) {
                                LogUtils.error(RABBITMQ_CONSUMER_ACK_ERROR, queueName, e.getMessage(), e);
                            }
                        });
            } catch (Exception e) {
                LogUtils.error(RABBITMQ_CONSUMER_PROCESS_ERROR, queueName, e.getMessage(), e);
                try {
                    channel.basicNack(deliveryTag, false, true);
                } catch (IOException ioEx) {
                    LogUtils.error(RABBITMQ_CONSUMER_NACK_ERROR, queueName, ioEx.getMessage(), ioEx);
                }
            }
        });

        LogUtils.debug(CREATE_CONTAINER_QUEUE, queueName);
        return container;
    }

    /**
     * 声明主交换机、主队列（绑定死信交换机）、死信交换机及死信队列。
     * 主队列消息过期或被拒绝后自动路由至死信队列。
     */
    private void declareInfrastructure(String queueName) {
        String dlxName = queueName + rabbitMQProperties.getDeadLetterExchangeSuffix();
        String dlqName = queueName + rabbitMQProperties.getDeadLetterRoutingKeySuffix();

        DirectExchange dlx = new DirectExchange(dlxName, true, false);
        Queue dlq = QueueBuilder.durable(dlqName).build();
        Binding dlqBinding = BindingBuilder.bind(dlq).to(dlx).with(dlqName);
        rabbitAdmin.declareExchange(dlx);
        rabbitAdmin.declareQueue(dlq);
        rabbitAdmin.declareBinding(dlqBinding);

        DirectExchange mainExchange = new DirectExchange(queueName, true, false);
        Queue mainQueue = QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", dlxName)
                .withArgument("x-dead-letter-routing-key", dlqName)
                .build();
        Binding mainBinding = BindingBuilder.bind(mainQueue).to(mainExchange).with(queueName);
        rabbitAdmin.declareExchange(mainExchange);
        rabbitAdmin.declareQueue(mainQueue);
        rabbitAdmin.declareBinding(mainBinding);
    }

    private Message convertMessage(org.springframework.amqp.core.Message amqpMessage) {
        Message message = new Message();
        message.setTopic(amqpMessage.getMessageProperties().getConsumerQueue());
        message.setKey(amqpMessage.getMessageProperties().getCorrelationId());
        message.setBody(new String(amqpMessage.getBody(), StandardCharsets.UTF_8));
        return message;
    }
}
