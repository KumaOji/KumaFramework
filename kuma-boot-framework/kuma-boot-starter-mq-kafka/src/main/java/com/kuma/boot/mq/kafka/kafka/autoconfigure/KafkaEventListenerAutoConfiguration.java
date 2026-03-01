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

import com.kuma.boot.common.utils.log.LogUtils;
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

    //消费者启动事件
    @Configuration
    public static class ConsumerStartedEventListener implements ApplicationListener<ConsumerStartedEvent> {

        @Override
        public void onApplicationEvent(ConsumerStartedEvent event) {

//			LogUtils.info("KafkaEventListener ----- ConsumerStartedEvent onApplicationEvent 消费者启动事件 {}", event);

            String listenerId = event.getSource(KafkaMessageListenerContainer.class).getListenerId();

            LogUtils.info("KafkaEventListener ----- ConsumerStartedEvent 消费者启动成功事件: {}, source:{}", listenerId, event);
        }
    }

    @Configuration
    public static class ConsumerPausedEventListener implements ApplicationListener<ConsumerPausedEvent> {

        @Override
        public void onApplicationEvent(ConsumerPausedEvent event) {
//			LogUtils.info("KafkaEventListener ----- ConsumerPausedEvent onApplicationEvent {}", event);

            String listenerId = event.getSource(KafkaMessageListenerContainer.class).getListenerId();

            LogUtils.info("KafkaEventListener ----- ConsumerPausedEvent 消费者已暂停事件: {}, source:{}", listenerId, event);
        }
    }


    @Configuration
    public static class ConsumerResumedEventListener implements ApplicationListener<ConsumerResumedEvent> {

        @Override
        public void onApplicationEvent(ConsumerResumedEvent event) {
//			LogUtils.info("KafkaEventListener ----- ConsumerResumedEvent onApplicationEvent {}", event);

            String listenerId = event.getSource(KafkaMessageListenerContainer.class).getListenerId();

            LogUtils.info("KafkaEventListener ----- ConsumerResumedEvent 消费者已恢复事件: {}, source:{}", listenerId, event);
        }
    }


    @Configuration
    public static class ConsumerStoppedEventListener implements ApplicationListener<ConsumerStoppedEvent> {

        @Override
        public void onApplicationEvent(ConsumerStoppedEvent event) {
//			LogUtils.info("KafkaEventListener ----- ConsumerStoppedEvent onApplicationEvent {}", event);

            String listenerId = event.getSource(KafkaMessageListenerContainer.class).getListenerId();

            LogUtils.info("KafkaEventListener ----- ConsumerStoppedEvent 消费者已停止事件: {}, source:{}", listenerId, event);
        }
    }

    @Configuration
    public static class ConsumerFailedToStartEventListener implements ApplicationListener<ConsumerFailedToStartEvent> {

        @Override
        public void onApplicationEvent(ConsumerFailedToStartEvent event) {
//			LogUtils.info("KafkaEventListener ----- ConsumerFailedToStartEvent onApplicationEvent {}", event);

            String listenerId = event.getSource(KafkaMessageListenerContainer.class).getListenerId();

            LogUtils.info("KafkaEventListener ----- ConsumerFailedToStartEvent 消费者启动失败事件: {}, source:{}", listenerId, event);
        }
    }

    @Configuration
    public static class ConsumerPartitionPausedEventEventListener
            implements ApplicationListener<ConsumerPartitionPausedEvent> {

        @Override
        public void onApplicationEvent(ConsumerPartitionPausedEvent event) {
//			LogUtils.info("KafkaEventListener ----- ConsumerPartitionPausedEvent onApplicationEvent {}", event);

            String topic = event.getPartition().topic();
            int partition = event.getPartition().partition();
            String listenerId = event.getSource(KafkaMessageListenerContainer.class).getListenerId();

            LogUtils.info(
                    "KafkaEventListener ----- ConsumerPartitionPausedEvent 分区暂停事件 topic:{}, partition:{}, listenerId:{}, source:{}",
                    topic, partition, listenerId, event);
        }
    }

    @Configuration
    public static class ConsumerPartitionResumedEventListener
            implements ApplicationListener<ConsumerPartitionResumedEvent> {

        @Override
        public void onApplicationEvent(ConsumerPartitionResumedEvent event) {
//			LogUtils.info("KafkaEventListener ----- ConsumerPartitionResumedEvent onApplicationEvent {}", event);
            String topic = event.getPartition().topic();
            int partition = event.getPartition().partition();
            String listenerId = event.getSource(KafkaMessageListenerContainer.class).getListenerId();

            LogUtils.info(
                    "KafkaEventListener ----- ConsumerPartitionResumedEvent 分区恢复事件 topic:{}, partition:{}, listenerId:{}, source:{}",
                    topic, partition, listenerId, event);
        }
    }


    @Configuration
    public static class ContainerStoppedEventListener implements ApplicationListener<ContainerStoppedEvent> {

        @Override
        public void onApplicationEvent(ContainerStoppedEvent event) {
//			LogUtils.info("KafkaEventListener ----- ContainerStoppedEvent onApplicationEvent {}", event);

            String listenerId = event.getSource(KafkaMessageListenerContainer.class).getListenerId();

            LogUtils.info("KafkaEventListener ----- ContainerStoppedEvent 容器停止事件: {}, source:{}", listenerId, event);
        }
    }

    @Configuration
    public static class ListenerContainerIdleEventListener implements ApplicationListener<ListenerContainerIdleEvent> {

        @Override
        public void onApplicationEvent(ListenerContainerIdleEvent event) {
//			LogUtils.info("KafkaEventListener ----- ListenerContainerIdleEvent onApplicationEvent {}", event);

            String listenerId = event.getListenerId();
            long idleTime = event.getIdleTime();

            LogUtils.info("KafkaEventListener ----- ListenerContainerIdleEvent 容器空闲事件 空闲时间:{}, listenerId:{}, source:{}",
                    idleTime, listenerId, event);

            // 空闲时间过长可以执行一些清理操作
            if (idleTime > 30000) { // 30秒
                //handleLongIdleTime(listenerId, idleTime);
            }
        }
    }

    @Configuration
    public static class ListenerContainerPartitionIdleEventListener
            implements ApplicationListener<ListenerContainerPartitionIdleEvent> {

        @Override
        public void onApplicationEvent(ListenerContainerPartitionIdleEvent event) {
//			LogUtils.info(
//				"KafkaEventListener ----- ListenerContainerPartitionIdleEvent" + " onApplicationEvent {}", event);

            String topic = event.getTopicPartition().topic();
            Integer partition = event.getTopicPartition().partition();
            long idleTime = event.getIdleTime();
            String listenerId = event.getListenerId();

            LogUtils.info(
                    "KafkaEventListener ----- ListenerContainerPartitionIdleEvent 分区空闲事件 topic:{}, partition:{}, idleTime:{}, listenerId:{}, source:{}",
                    topic, partition, idleTime, listenerId, event);

        }
    }

    @Configuration
    public static class NonResponsiveConsumerEventListener implements ApplicationListener<NonResponsiveConsumerEvent> {

        @Override
        public void onApplicationEvent(NonResponsiveConsumerEvent event) {
            LogUtils.info("KafkaEventListener ----- NonResponsiveConsumerEvent onApplicationEvent {}", event);
        }
    }


    @Component
    public static class KafkaEventMonitor {

        private final KafkaEventMetrics metrics = new KafkaEventMetrics();

        @Async
        @EventListener
        public void monitorAllKafkaEvents(KafkaEvent event) {
            metrics.recordEvent(event.getClass().getSimpleName());

            if (event instanceof ConsumerFailedToStartEvent) {
                metrics.incrementFailureCount();
            } else if (event instanceof ConsumerStartedEvent) {
                metrics.incrementStartCount();
            }
        }

        // 获取监控数据
        public KafkaEventMetrics getMetrics() {
            return metrics;
        }

        public static class KafkaEventMetrics {

            private int startCount = 0;
            private int failureCount = 0;
            private java.util.Map<String, Integer> eventCounts = new java.util.HashMap<>();

            public void recordEvent(String eventType) {
                eventCounts.put(eventType, eventCounts.getOrDefault(eventType, 0) + 1);
            }

            public void incrementStartCount() {
                startCount++;
            }

            public void incrementFailureCount() {
                failureCount++;
            }

            // getter方法
            public int getStartCount() {
                return startCount;
            }

            public int getFailureCount() {
                return failureCount;
            }

            public java.util.Map<String, Integer> getEventCounts() {
                return eventCounts;
            }
        }
    }

//	@Configuration
//	@EnableAsync
//	public class ComprehensiveKafkaEventListener {
//
//		// 异步处理事件，避免阻塞主线程
//		@Async
//		@EventListener
//		public void handleKafkaEvent(Object event) {
//			if (event instanceof ConsumerFailedToStartEvent) {
//				handleConsumerFailedToStart((ConsumerFailedToStartEvent) event);
//			} else if (event instanceof ConsumerStartedEvent) {
//				handleConsumerStarted((ConsumerStartedEvent) event);
//			} else if (event instanceof ConsumerStoppedEvent) {
//				handleConsumerStopped((ConsumerStoppedEvent) event);
//			} else if (event instanceof ConsumerPausedEvent) {
//				handleConsumerPaused((ConsumerPausedEvent) event);
//			} else if (event instanceof ConsumerResumedEvent) {
//				handleConsumerResumed((ConsumerResumedEvent) event);
//			} else if (event instanceof ConsumerPartitionPausedEvent) {
//				handlePartitionPaused((ConsumerPartitionPausedEvent) event);
//			} else if (event instanceof ConsumerPartitionResumedEvent) {
//				handlePartitionResumed((ConsumerPartitionResumedEvent) event);
//			} else if (event instanceof ListenerContainerIdleEvent) {
//				handleContainerIdle((ListenerContainerIdleEvent) event);
//			} else if (event instanceof ListenerContainerPartitionIdleEvent) {
//				handlePartitionIdle((ListenerContainerPartitionIdleEvent) event);
//			}
//		}
//
//		// 各个事件处理方法（同上）
//		private void handleConsumerFailedToStart(ConsumerFailedToStartEvent event) {
//			// 实现同上
//		}
//
//		private void handleConsumerStarted(ConsumerStartedEvent event) {
//			// 实现同上
//		}
//
//		// 其他方法...
//	}
}
