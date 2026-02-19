/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.context.ApplicationListener
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.event.EventListener
 *  org.springframework.kafka.event.ConsumerFailedToStartEvent
 *  org.springframework.kafka.event.ConsumerPartitionPausedEvent
 *  org.springframework.kafka.event.ConsumerPartitionResumedEvent
 *  org.springframework.kafka.event.ConsumerPausedEvent
 *  org.springframework.kafka.event.ConsumerResumedEvent
 *  org.springframework.kafka.event.ConsumerStartedEvent
 *  org.springframework.kafka.event.ConsumerStoppedEvent
 *  org.springframework.kafka.event.ContainerStoppedEvent
 *  org.springframework.kafka.event.KafkaEvent
 *  org.springframework.kafka.event.ListenerContainerIdleEvent
 *  org.springframework.kafka.event.ListenerContainerPartitionIdleEvent
 *  org.springframework.kafka.event.NonResponsiveConsumerEvent
 *  org.springframework.kafka.listener.KafkaMessageListenerContainer
 *  org.springframework.scheduling.annotation.Async
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.mq.kafka.kafka.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.event.ConsumerFailedToStartEvent;
import org.springframework.kafka.event.ConsumerPartitionPausedEvent;
import org.springframework.kafka.event.ConsumerPartitionResumedEvent;
import org.springframework.kafka.event.ConsumerPausedEvent;
import org.springframework.kafka.event.ConsumerResumedEvent;
import org.springframework.kafka.event.ConsumerStartedEvent;
import org.springframework.kafka.event.ConsumerStoppedEvent;
import org.springframework.kafka.event.ContainerStoppedEvent;
import org.springframework.kafka.event.KafkaEvent;
import org.springframework.kafka.event.ListenerContainerIdleEvent;
import org.springframework.kafka.event.ListenerContainerPartitionIdleEvent;
import org.springframework.kafka.event.NonResponsiveConsumerEvent;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@AutoConfiguration
public class KafkaEventListenerAutoConfiguration {

    @Component
    public static class KafkaEventMonitor {
        private final KafkaEventMetrics metrics = new KafkaEventMetrics();

        @Async
        @EventListener
        public void monitorAllKafkaEvents(KafkaEvent event) {
            this.metrics.recordEvent(event.getClass().getSimpleName());
            if (event instanceof ConsumerFailedToStartEvent) {
                this.metrics.incrementFailureCount();
            } else if (event instanceof ConsumerStartedEvent) {
                this.metrics.incrementStartCount();
            }
        }

        public KafkaEventMetrics getMetrics() {
            return this.metrics;
        }

        public static class KafkaEventMetrics {
            private int startCount = 0;
            private int failureCount = 0;
            private Map<String, Integer> eventCounts = new HashMap<String, Integer>();

            public void recordEvent(String eventType) {
                this.eventCounts.put(eventType, this.eventCounts.getOrDefault(eventType, 0) + 1);
            }

            public void incrementStartCount() {
                ++this.startCount;
            }

            public void incrementFailureCount() {
                ++this.failureCount;
            }

            public int getStartCount() {
                return this.startCount;
            }

            public int getFailureCount() {
                return this.failureCount;
            }

            public Map<String, Integer> getEventCounts() {
                return this.eventCounts;
            }
        }
    }

    @Configuration
    public static class NonResponsiveConsumerEventListener
    implements ApplicationListener<NonResponsiveConsumerEvent> {
        public void onApplicationEvent(NonResponsiveConsumerEvent event) {
            LogUtils.info((String)"KafkaEventListener ----- NonResponsiveConsumerEvent onApplicationEvent {}", (Object[])new Object[]{event});
        }
    }

    @Configuration
    public static class ListenerContainerPartitionIdleEventListener
    implements ApplicationListener<ListenerContainerPartitionIdleEvent> {
        public void onApplicationEvent(ListenerContainerPartitionIdleEvent event) {
            String topic = event.getTopicPartition().topic();
            Integer partition = event.getTopicPartition().partition();
            long idleTime = event.getIdleTime();
            String listenerId = event.getListenerId();
            LogUtils.info((String)"KafkaEventListener ----- ListenerContainerPartitionIdleEvent \u5206\u533a\u7a7a\u95f2\u4e8b\u4ef6 topic:{}, partition:{}, idleTime:{}, listenerId:{}, source:{}", (Object[])new Object[]{topic, partition, idleTime, listenerId, event});
        }
    }

    @Configuration
    public static class ListenerContainerIdleEventListener
    implements ApplicationListener<ListenerContainerIdleEvent> {
        public void onApplicationEvent(ListenerContainerIdleEvent event) {
            String listenerId = event.getListenerId();
            long idleTime = event.getIdleTime();
            LogUtils.info((String)"KafkaEventListener ----- ListenerContainerIdleEvent \u5bb9\u5668\u7a7a\u95f2\u4e8b\u4ef6 \u7a7a\u95f2\u65f6\u95f4:{}, listenerId:{}, source:{}", (Object[])new Object[]{idleTime, listenerId, event});
            if (idleTime > 30000L) {
                // empty if block
            }
        }
    }

    @Configuration
    public static class ContainerStoppedEventListener
    implements ApplicationListener<ContainerStoppedEvent> {
        public void onApplicationEvent(ContainerStoppedEvent event) {
            String listenerId = ((KafkaMessageListenerContainer)event.getSource(KafkaMessageListenerContainer.class)).getListenerId();
            LogUtils.info((String)"KafkaEventListener ----- ContainerStoppedEvent \u5bb9\u5668\u505c\u6b62\u4e8b\u4ef6: {}, source:{}", (Object[])new Object[]{listenerId, event});
        }
    }

    @Configuration
    public static class ConsumerPartitionResumedEventListener
    implements ApplicationListener<ConsumerPartitionResumedEvent> {
        public void onApplicationEvent(ConsumerPartitionResumedEvent event) {
            String topic = event.getPartition().topic();
            int partition = event.getPartition().partition();
            String listenerId = ((KafkaMessageListenerContainer)event.getSource(KafkaMessageListenerContainer.class)).getListenerId();
            LogUtils.info((String)"KafkaEventListener ----- ConsumerPartitionResumedEvent \u5206\u533a\u6062\u590d\u4e8b\u4ef6 topic:{}, partition:{}, listenerId:{}, source:{}", (Object[])new Object[]{topic, partition, listenerId, event});
        }
    }

    @Configuration
    public static class ConsumerPartitionPausedEventEventListener
    implements ApplicationListener<ConsumerPartitionPausedEvent> {
        public void onApplicationEvent(ConsumerPartitionPausedEvent event) {
            String topic = event.getPartition().topic();
            int partition = event.getPartition().partition();
            String listenerId = ((KafkaMessageListenerContainer)event.getSource(KafkaMessageListenerContainer.class)).getListenerId();
            LogUtils.info((String)"KafkaEventListener ----- ConsumerPartitionPausedEvent \u5206\u533a\u6682\u505c\u4e8b\u4ef6 topic:{}, partition:{}, listenerId:{}, source:{}", (Object[])new Object[]{topic, partition, listenerId, event});
        }
    }

    @Configuration
    public static class ConsumerFailedToStartEventListener
    implements ApplicationListener<ConsumerFailedToStartEvent> {
        public void onApplicationEvent(ConsumerFailedToStartEvent event) {
            String listenerId = ((KafkaMessageListenerContainer)event.getSource(KafkaMessageListenerContainer.class)).getListenerId();
            LogUtils.info((String)"KafkaEventListener ----- ConsumerFailedToStartEvent \u6d88\u8d39\u8005\u542f\u52a8\u5931\u8d25\u4e8b\u4ef6: {}, source:{}", (Object[])new Object[]{listenerId, event});
        }
    }

    @Configuration
    public static class ConsumerStoppedEventListener
    implements ApplicationListener<ConsumerStoppedEvent> {
        public void onApplicationEvent(ConsumerStoppedEvent event) {
            String listenerId = ((KafkaMessageListenerContainer)event.getSource(KafkaMessageListenerContainer.class)).getListenerId();
            LogUtils.info((String)"KafkaEventListener ----- ConsumerStoppedEvent \u6d88\u8d39\u8005\u5df2\u505c\u6b62\u4e8b\u4ef6: {}, source:{}", (Object[])new Object[]{listenerId, event});
        }
    }

    @Configuration
    public static class ConsumerResumedEventListener
    implements ApplicationListener<ConsumerResumedEvent> {
        public void onApplicationEvent(ConsumerResumedEvent event) {
            String listenerId = ((KafkaMessageListenerContainer)event.getSource(KafkaMessageListenerContainer.class)).getListenerId();
            LogUtils.info((String)"KafkaEventListener ----- ConsumerResumedEvent \u6d88\u8d39\u8005\u5df2\u6062\u590d\u4e8b\u4ef6: {}, source:{}", (Object[])new Object[]{listenerId, event});
        }
    }

    @Configuration
    public static class ConsumerPausedEventListener
    implements ApplicationListener<ConsumerPausedEvent> {
        public void onApplicationEvent(ConsumerPausedEvent event) {
            String listenerId = ((KafkaMessageListenerContainer)event.getSource(KafkaMessageListenerContainer.class)).getListenerId();
            LogUtils.info((String)"KafkaEventListener ----- ConsumerPausedEvent \u6d88\u8d39\u8005\u5df2\u6682\u505c\u4e8b\u4ef6: {}, source:{}", (Object[])new Object[]{listenerId, event});
        }
    }

    @Configuration
    public static class ConsumerStartedEventListener
    implements ApplicationListener<ConsumerStartedEvent> {
        public void onApplicationEvent(ConsumerStartedEvent event) {
            String listenerId = ((KafkaMessageListenerContainer)event.getSource(KafkaMessageListenerContainer.class)).getListenerId();
            LogUtils.info((String)"KafkaEventListener ----- ConsumerStartedEvent \u6d88\u8d39\u8005\u542f\u52a8\u6210\u529f\u4e8b\u4ef6: {}, source:{}", (Object[])new Object[]{listenerId, event});
        }
    }
}

