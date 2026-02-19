/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.kuma.boot.mq.common;

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(name={"kuma.boot.mq.enabled"}, matchIfMissing=true)
@EnableConfigurationProperties(value={MessageQueueProperties.class})
@Configuration(proxyBeanMethods=false)
public class MessageQueueAutoConfiguration {
    public static final String AUTOWIRED_MESSAGE_QUEUE_FACTORY = "Autowired MessageQueueProviderFactory";
    private final MessageQueueProperties messageQueueProperties;

    public MessageQueueAutoConfiguration(MessageQueueProperties messageQueueProperties) {
        this.messageQueueProperties = messageQueueProperties;
    }

    @Bean
    public MessageQueueProviderFactory messageQueueProviderFactory() {
        LogUtils.debug((String)AUTOWIRED_MESSAGE_QUEUE_FACTORY, (Object[])new Object[0]);
        return new MessageQueueProviderFactory(this.messageQueueProperties.getType());
    }
}

