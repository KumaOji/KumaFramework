package com.kuma.boot.eventbus.disruptor.tmp3.context;

import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorApplicationEvent;
import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class DisruptorApplicationContext implements ApplicationContextAware, DisruptorEventPublisher {
   protected ApplicationContext applicationContext;

   public DisruptorApplicationContext() {
   }

   public void publishEvent(DisruptorEvent event) {
      this.applicationContext.publishEvent(new DisruptorApplicationEvent(event));
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.applicationContext = applicationContext;
   }

   public ApplicationContext getApplicationContext() {
      return this.applicationContext;
   }
}
