package com.kuma.boot.eventbus.event.config;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.eventbus.event.annotation.MessageEventBus;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class EventBusTemplate {
   private final EventBus syncEventBus;
   private final AsyncEventBus asyncEventBus;

   public EventBusTemplate(EventBus syncEventBus, AsyncEventBus asyncEventBus) {
      this.syncEventBus = syncEventBus;
      this.asyncEventBus = asyncEventBus;
   }

   public void postSync(Object event) {
      this.syncEventBus.post(event);
   }

   public void postAsync(Object event) {
      this.asyncEventBus.post(event);
   }

   @PostConstruct
   public void initialize() {
      for(Object listener : ContextUtils.getBeansWithAnnotationList(MessageEventBus.class)) {
         this.asyncEventBus.register(listener);
         this.syncEventBus.register(listener);
      }

   }
}
