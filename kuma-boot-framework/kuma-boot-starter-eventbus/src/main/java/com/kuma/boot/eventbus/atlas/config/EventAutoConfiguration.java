package com.kuma.boot.eventbus.atlas.config;

import com.kuma.boot.eventbus.atlas.core.DefaultEventBus;
import com.kuma.boot.eventbus.atlas.core.EventBus;
import com.kuma.boot.eventbus.atlas.processor.EventAnnotationProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Atlas 事件总线自动配置：注册 {@link EventBus} 与 {@link EventAnnotationProcessor}。
 * <p>必须使用 {@link Bean} 方法声明处理器并注入 {@link EventBus}：若使用 {@code @Import(Processor.class)}，
 * Spring 可能先于本类中的 {@code eventBus()} 去实例化 {@link BeanPostProcessor}，导致仍报缺少 {@link EventBus}。</p>
 * <p>须由 {@code META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports} 加载。</p>
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
   public EventAnnotationProcessor eventAnnotationProcessor( EventBus eventBus ) {
      return new EventAnnotationProcessor(eventBus);
   }
}
