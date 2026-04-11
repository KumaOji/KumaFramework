package com.kuma.boot.eventbus.disruptor.tmp.support;

import com.lmax.disruptor.EventHandler;
import com.kuma.boot.eventbus.disruptor.tmp.DisruptorEventConsumer;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.ResolvableType;

public class SpringEventHandler<T> implements EventHandler<DisruptorEventWrapper<T>> {
   private final ObjectProvider<DisruptorEventConsumer<T>> disruptorEventConsumers;

   public SpringEventHandler(ObjectProvider<DisruptorEventConsumer<T>> disruptorEventConsumers) {
      this.disruptorEventConsumers = disruptorEventConsumers;
   }

   public SpringEventHandler(BeanFactory beanFactory, Class<T> type) {
      ResolvableType resolvableType = ResolvableType.forClassWithGenerics(DisruptorEventConsumer.class, new Class[]{type});
      this.disruptorEventConsumers = beanFactory.getBeanProvider(resolvableType);
   }

   public void onEvent(DisruptorEventWrapper<T> wrapper, long sequence, boolean endOfBatch) throws Exception {
      for(DisruptorEventConsumer<T> disruptorEventConsumer : this.disruptorEventConsumers) {
         disruptorEventConsumer.onEvent(wrapper.unwrap(), sequence, endOfBatch);
      }

   }
}
