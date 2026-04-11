package com.kuma.boot.eventbus.disruptor.tmp.factory;

import com.lmax.disruptor.EventFactory;
import com.kuma.boot.eventbus.disruptor.tmp.support.DisruptorEventWrapper;

public class SpringEventFactory<T> implements EventFactory<DisruptorEventWrapper<T>> {
   public SpringEventFactory() {
   }

   public DisruptorEventWrapper<T> newInstance() {
      return new DisruptorEventWrapper<T>();
   }
}
