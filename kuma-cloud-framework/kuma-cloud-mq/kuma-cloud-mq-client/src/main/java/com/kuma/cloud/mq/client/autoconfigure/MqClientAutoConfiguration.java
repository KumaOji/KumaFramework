package com.kuma.cloud.mq.client.autoconfigure;

import com.kuma.cloud.mq.client.consumer.core.MqConsumerPush;
import com.kuma.cloud.mq.client.producer.core.MqProducer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * MqClientAutoConfiguration — 自定义 MQ 客户端自动配置
 * <p>
 * 激活条件: kuma.mq.client.enabled=true
 * 注册 {@link MqProducer}（生产者）和 {@link MqConsumerPush}（推式消费者）两个 Bean，
 * 均已完成连接参数绑定，需调用方手动调用 {@code .start()} 建立连接。
 *
 * @author kuma
 * @version 2026.02
 */
@AutoConfiguration
@EnableConfigurationProperties(MqClientProperties.class)
@ConditionalOnProperty(prefix = "kuma.mq.client", name = "enabled", havingValue = "true", matchIfMissing = false)
public class MqClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MqProducer mqProducer(MqClientProperties properties) {
        return (MqProducer) new MqProducer()
                .brokerAddress(properties.getBrokerAddress())
                .groupName(properties.getGroupName())
                .appKey(properties.getAppKey())
                .appSecret(properties.getAppSecret())
                .respTimeoutMills(properties.getRespTimeoutMills())
                .check(properties.isCheck());
    }

    @Bean
    @ConditionalOnMissingBean
    public MqConsumerPush mqConsumerPush(MqClientProperties properties) {
        return new MqConsumerPush()
                .brokerAddress(properties.getBrokerAddress())
                .groupName(properties.getGroupName())
                .appKey(properties.getAppKey())
                .appSecret(properties.getAppSecret())
                .respTimeoutMills(properties.getRespTimeoutMills())
                .check(properties.isCheck());
    }
}
