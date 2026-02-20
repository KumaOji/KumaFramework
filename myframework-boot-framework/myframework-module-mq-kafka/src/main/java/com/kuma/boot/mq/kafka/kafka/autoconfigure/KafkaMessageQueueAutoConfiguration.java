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

package com.kuma.boot.mq.kafka.kafka.autoconfigure;

import com.kuma.boot.common.support.thread.MDCTaskDecorator;
import com.kuma.boot.common.support.thread.MDCThreadPoolTaskExecutor;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.mq.common.MessageQueueAutoConfiguration;
import com.kuma.boot.mq.common.MessageQueueConsumer;
import com.kuma.boot.mq.common.MessageQueueProperties;
import com.kuma.boot.mq.common.MessageQueueProvider;
import com.kuma.boot.mq.common.MessageQueueProviderFactory;
import com.kuma.boot.mq.kafka.kafka.core.KafkaConsumer;
import com.kuma.boot.mq.kafka.kafka.core.KafkaProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Kafka 自动配置
 */
@AutoConfigureAfter(MessageQueueAutoConfiguration.class)
@ConditionalOnBean(KafkaProperties.class)
@ConditionalOnClass(KafkaTemplate.class)
@ConditionalOnProperty(
        prefix = com.kuma.boot.mq.kafka.kafka.autoconfigure.properties.KafkaProperties.PREFIX,
        name = "enabled",
        havingValue = "true")
@Configuration(proxyBeanMethods = false)
public class KafkaMessageQueueAutoConfiguration {

    public static final String TYPE = "KAFKA";

    public static final String BEAN_CONSUMER = "kafkaConsumer";

    public static final String BEAN_PROVIDER = "kafkaProvider";

    public static final String AUTOWIRED_KAKFA_CONSUMER = "Autowired KafkaConsumer";

    public static final String AUTOWIRED_KAFKA_PROVIDER = "Autowired KafkaProvider";

    @Bean("kafkaConsumerExecutor")
    public TaskExecutor kafkaConsumerExecutor() {
        MDCThreadPoolTaskExecutor executor = new MDCThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(10000);
        executor.setKeepAliveSeconds(300);
        executor.setThreadNamePrefix("kmc-kafka-consumer-executor");

        executor.setTaskDecorator(new MDCTaskDecorator());

        /*
        rejection-policy：当pool已经达到max size的时候，如何处理新任务
        CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        */
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();

        return executor;
    }

    @Bean(BEAN_CONSUMER)
    public KafkaConsumer kafkaConsumer(
            MessageQueueProperties messageQueueProperties,
            KafkaProperties kafkaProperties,
            ObjectProvider<List<MessageQueueConsumer>> messageListeners,
            ObjectProvider<ConsumerFactory<String, String>> consumerFactory,
            @Autowired @Qualifier("kafkaConsumerExecutor") TaskExecutor taskExecutor) {
        LogUtils.debug(AUTOWIRED_KAKFA_CONSUMER);
        return new KafkaConsumer(
                messageQueueProperties,
                kafkaProperties,
                messageListeners.getIfAvailable(),
                consumerFactory.getIfAvailable(),
                taskExecutor);
    }

    @Bean(BEAN_PROVIDER)
    public MessageQueueProvider messageQueueProvider(KafkaTemplate<String, String> kafkaTemplate) {
        LogUtils.debug(AUTOWIRED_KAFKA_PROVIDER);
        MessageQueueProviderFactory.addBean(TYPE, BEAN_PROVIDER);
        return new KafkaProvider(kafkaTemplate);
    }
}
