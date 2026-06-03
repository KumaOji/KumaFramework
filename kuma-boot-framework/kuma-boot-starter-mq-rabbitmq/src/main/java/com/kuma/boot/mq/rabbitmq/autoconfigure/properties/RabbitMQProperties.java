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

package com.kuma.boot.mq.rabbitmq.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * RabbitMQ 扩展配置属性
 */
@RefreshScope
@ConfigurationProperties(prefix = RabbitMQProperties.PREFIX)
public class RabbitMQProperties {

    public static final String PREFIX = "kuma.boot.mq.rabbitmq";

    /** 是否启用 */
    private boolean enabled = false;

    /** 是否自动声明 Exchange / Queue / Binding */
    private boolean autoDeclare = true;

    /** 死信交换机后缀，最终名称 = queueName + suffix */
    private String deadLetterExchangeSuffix = ".dlx";

    /** 死信队列路由键后缀，最终名称 = queueName + suffix */
    private String deadLetterRoutingKeySuffix = ".dead";

    /** 最小并发消费者数 */
    private int concurrentConsumers = 1;

    /** 最大并发消费者数 */
    private int maxConcurrentConsumers = 10;

    /** 每次预取消息数（QoS prefetch） */
    private int prefetchCount = 1;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAutoDeclare() {
        return autoDeclare;
    }

    public void setAutoDeclare(boolean autoDeclare) {
        this.autoDeclare = autoDeclare;
    }

    public String getDeadLetterExchangeSuffix() {
        return deadLetterExchangeSuffix;
    }

    public void setDeadLetterExchangeSuffix(String deadLetterExchangeSuffix) {
        this.deadLetterExchangeSuffix = deadLetterExchangeSuffix;
    }

    public String getDeadLetterRoutingKeySuffix() {
        return deadLetterRoutingKeySuffix;
    }

    public void setDeadLetterRoutingKeySuffix(String deadLetterRoutingKeySuffix) {
        this.deadLetterRoutingKeySuffix = deadLetterRoutingKeySuffix;
    }

    public int getConcurrentConsumers() {
        return concurrentConsumers;
    }

    public void setConcurrentConsumers(int concurrentConsumers) {
        this.concurrentConsumers = concurrentConsumers;
    }

    public int getMaxConcurrentConsumers() {
        return maxConcurrentConsumers;
    }

    public void setMaxConcurrentConsumers(int maxConcurrentConsumers) {
        this.maxConcurrentConsumers = maxConcurrentConsumers;
    }

    public int getPrefetchCount() {
        return prefetchCount;
    }

    public void setPrefetchCount(int prefetchCount) {
        this.prefetchCount = prefetchCount;
    }
}
