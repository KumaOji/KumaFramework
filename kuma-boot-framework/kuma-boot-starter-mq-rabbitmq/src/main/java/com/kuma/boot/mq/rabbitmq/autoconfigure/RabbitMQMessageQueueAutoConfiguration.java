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

package com.kuma.boot.mq.rabbitmq.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.mq.common.MessageQueueAutoConfiguration;
import com.kuma.boot.mq.common.MessageQueueConsumer;
import com.kuma.boot.mq.common.MessageQueueProperties;
import com.kuma.boot.mq.common.MessageQueueProvider;
import com.kuma.boot.mq.common.MessageQueueProviderFactory;
import com.kuma.boot.mq.rabbitmq.autoconfigure.properties.RabbitMQProperties;
import com.kuma.boot.mq.rabbitmq.core.RabbitMQConsumer;
import com.kuma.boot.mq.rabbitmq.core.RabbitMQProvider;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * RabbitMQ 消息队列自动配置
 */
@AutoConfigureAfter(MessageQueueAutoConfiguration.class)
@ConditionalOnClass(RabbitTemplate.class)
@ConditionalOnProperty(
        prefix = RabbitMQProperties.PREFIX,
        name = "enabled",
        havingValue = "true")
@EnableConfigurationProperties(RabbitMQProperties.class)
@Configuration(proxyBeanMethods = false)
public class RabbitMQMessageQueueAutoConfiguration {

    public static final String TYPE = "RABBITMQ";

    public static final String BEAN_CONSUMER = "rabbitMQConsumer";

    public static final String BEAN_PROVIDER = "rabbitMQProvider";

    private static final String AUTOWIRED_RABBITMQ_CONSUMER = "Autowired RabbitMQConsumer";

    private static final String AUTOWIRED_RABBITMQ_PROVIDER = "Autowired RabbitMQProvider";

    @ConditionalOnMissingBean
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean(BEAN_CONSUMER)
    public RabbitMQConsumer rabbitMQConsumer(
            MessageQueueProperties messageQueueProperties,
            RabbitMQProperties rabbitMQProperties,
            ObjectProvider<List<MessageQueueConsumer>> messageListeners,
            ConnectionFactory connectionFactory,
            RabbitAdmin rabbitAdmin) {
        LogUtils.debug(AUTOWIRED_RABBITMQ_CONSUMER);
        return new RabbitMQConsumer(
                messageQueueProperties,
                rabbitMQProperties,
                messageListeners.getIfAvailable(),
                connectionFactory,
                rabbitAdmin);
    }

    @Bean(BEAN_PROVIDER)
    public MessageQueueProvider messageQueueProvider(RabbitTemplate rabbitTemplate) {
        LogUtils.debug(AUTOWIRED_RABBITMQ_PROVIDER);
        MessageQueueProviderFactory.addBean(TYPE, BEAN_PROVIDER);
        return new RabbitMQProvider(rabbitTemplate);
    }
}
