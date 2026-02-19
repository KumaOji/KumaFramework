/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.support.thread.MDCTaskDecorator
 *  com.kuma.boot.common.support.thread.MDCThreadPoolTaskExecutor
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.mq.common.MessageQueueAutoConfiguration
 *  com.kuma.boot.mq.common.MessageQueueConsumer
 *  com.kuma.boot.mq.common.MessageQueueProperties
 *  com.kuma.boot.mq.common.MessageQueueProvider
 *  com.kuma.boot.mq.common.MessageQueueProviderFactory
 *  org.springframework.beans.factory.ObjectProvider
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.boot.autoconfigure.AutoConfigureAfter
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.kafka.autoconfigure.KafkaProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.core.task.TaskDecorator
 *  org.springframework.core.task.TaskExecutor
 *  org.springframework.kafka.core.ConsumerFactory
 *  org.springframework.kafka.core.KafkaTemplate
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
import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
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
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@AutoConfigureAfter(value={MessageQueueAutoConfiguration.class})
@ConditionalOnBean(value={KafkaProperties.class})
@ConditionalOnClass(value={KafkaTemplate.class})
@ConditionalOnProperty(prefix="kuma.boot.mq.kafka", name={"enabled"}, havingValue="true")
@Configuration(proxyBeanMethods=false)
public class KafkaMessageQueueAutoConfiguration {
    public static final String TYPE = "KAFKA";
    public static final String BEAN_CONSUMER = "kafkaConsumer";
    public static final String BEAN_PROVIDER = "kafkaProvider";
    public static final String AUTOWIRED_KAKFA_CONSUMER = "Autowired KafkaConsumer";
    public static final String AUTOWIRED_KAFKA_PROVIDER = "Autowired KafkaProvider";

    @Bean(value={"kafkaConsumerExecutor"})
    public TaskExecutor kafkaConsumerExecutor() {
        MDCThreadPoolTaskExecutor executor = new MDCThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(10000);
        executor.setKeepAliveSeconds(300);
        executor.setThreadNamePrefix("kmc-kafka-consumer-executor");
        executor.setTaskDecorator((TaskDecorator)new MDCTaskDecorator());
        executor.setRejectedExecutionHandler((RejectedExecutionHandler)new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean(value={"kafkaConsumer"})
    public KafkaConsumer kafkaConsumer(MessageQueueProperties messageQueueProperties, KafkaProperties kafkaProperties, ObjectProvider<List<MessageQueueConsumer>> messageListeners, ObjectProvider<ConsumerFactory<String, String>> consumerFactory, @Autowired @Qualifier(value="kafkaConsumerExecutor") TaskExecutor taskExecutor) {
        LogUtils.debug((String)AUTOWIRED_KAKFA_CONSUMER, (Object[])new Object[0]);
        return new KafkaConsumer(messageQueueProperties, kafkaProperties, (List)messageListeners.getIfAvailable(), (ConsumerFactory<String, String>)((ConsumerFactory)consumerFactory.getIfAvailable()), taskExecutor);
    }

    @Bean(value={"kafkaProvider"})
    public MessageQueueProvider messageQueueProvider(KafkaTemplate<String, String> kafkaTemplate) {
        LogUtils.debug((String)AUTOWIRED_KAFKA_PROVIDER, (Object[])new Object[0]);
        MessageQueueProviderFactory.addBean((String)TYPE, (String)BEAN_PROVIDER);
        return new KafkaProvider(kafkaTemplate);
    }
}

