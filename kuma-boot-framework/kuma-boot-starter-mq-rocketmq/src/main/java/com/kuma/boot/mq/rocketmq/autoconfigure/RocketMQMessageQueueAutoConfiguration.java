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

package com.kuma.boot.mq.rocketmq.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.mq.common.MessageQueueAutoConfiguration;
import com.kuma.boot.mq.common.MessageQueueConsumer;
import com.kuma.boot.mq.common.MessageQueueProperties;
import com.kuma.boot.mq.common.MessageQueueProvider;
import com.kuma.boot.mq.common.MessageQueueProviderFactory;
import com.kuma.boot.mq.rocketmq.autoconfigure.properties.RocketMQProperties;
import com.kuma.boot.mq.rocketmq.core.RocketMQConsumer;
import com.kuma.boot.mq.rocketmq.core.RocketMQProvider;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * RocketMQ 消息队列自动配置
 */
@AutoConfigureAfter(MessageQueueAutoConfiguration.class)
@ConditionalOnClass(RocketMQTemplate.class)
@ConditionalOnProperty(
        prefix = RocketMQProperties.PREFIX,
        name = "enabled",
        havingValue = "true")
@Configuration(proxyBeanMethods = false)
public class RocketMQMessageQueueAutoConfiguration {

    public static final String TYPE = "ROCKETMQ";

    public static final String BEAN_CONSUMER = "rocketMQConsumer";

    public static final String BEAN_PROVIDER = "rocketMQProvider";

    public static final String AUTOWIRED_ROCKETMQ_CONSUMER = "Autowired RocketMQConsumer";

    public static final String AUTOWIRED_ROCKETMQ_PROVIDER = "Autowired RocketMQProvider";

    @Bean(BEAN_CONSUMER)
    public RocketMQConsumer rocketMQConsumer(
            MessageQueueProperties messageQueueProperties,
            org.apache.rocketmq.spring.autoconfigure.RocketMQProperties rocketMQProperties,
            ObjectProvider<List<MessageQueueConsumer>> messageListeners) {
        LogUtils.debug(AUTOWIRED_ROCKETMQ_CONSUMER);
        return new RocketMQConsumer(
                messageQueueProperties,
                rocketMQProperties,
                messageListeners.getIfAvailable());
    }

    @Bean(BEAN_PROVIDER)
    public MessageQueueProvider messageQueueProvider(RocketMQTemplate rocketMQTemplate) {
        LogUtils.debug(AUTOWIRED_ROCKETMQ_PROVIDER);
        MessageQueueProviderFactory.addBean(TYPE, BEAN_PROVIDER);
        return new RocketMQProvider(rocketMQTemplate);
    }
}
