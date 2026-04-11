package com.kuma.boot.eventbus.atlas.config;

import com.kuma.boot.eventbus.atlas.core.DefaultEventBus;
import com.kuma.boot.eventbus.atlas.core.EventBus;
import com.kuma.boot.eventbus.atlas.processor.EventAnnotationProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnClass({EventBus.class})
@EnableConfigurationProperties({EventProperties.class})
@Import({EventAnnotationProcessor.class})
public class EventAutoConfiguration {
   public EventAutoConfiguration() {
   }

   @Bean
   @ConditionalOnMissingBean
   public EventBus eventBus() {
      return new DefaultEventBus();
   }
}
