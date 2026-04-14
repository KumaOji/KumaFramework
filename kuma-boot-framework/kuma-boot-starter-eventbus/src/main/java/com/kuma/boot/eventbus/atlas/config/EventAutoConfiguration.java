package com.kuma.boot.eventbus.atlas.config;

import com.kuma.boot.eventbus.atlas.core.DefaultEventBus;
import com.kuma.boot.eventbus.atlas.core.EventBus;
import com.kuma.boot.eventbus.atlas.processor.EventAnnotationProcessor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Atlas 事件总线自动配置：注册 {@link EventBus} 与 {@link EventAnnotationProcessor}。
 * <p>处理器为 {@link org.springframework.beans.factory.config.BeanPostProcessor}，实例化阶段早于普通单例；
 * 处理器内通过 {@link ObjectProvider} 延迟获取 {@link EventBus}，避免 BPP 早期阶段解析不到总线 Bean。</p>
 */
@AutoConfiguration
@ConditionalOnClass({EventBus.class})
@EnableConfigurationProperties({EventProperties.class})
public class EventAutoConfiguration {
   public EventAutoConfiguration() {
   }

   @Bean
   @ConditionalOnMissingBean(EventBus.class)
   public EventBus eventBus() {
      return new DefaultEventBus();
   }

   @Bean
   @ConditionalOnMissingBean(EventAnnotationProcessor.class)
   public EventAnnotationProcessor eventAnnotationProcessor( ObjectProvider<EventBus> eventBusProvider ) {
      return new EventAnnotationProcessor(eventBusProvider);
   }
}
