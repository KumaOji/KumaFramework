package com.kuma.boot.eventbus.disruptor.tmp1.event;

import com.lmax.disruptor.EventFactory;

public class OrderlyDisruptorEventFactory<T> implements EventFactory<DataEvent<T>> {
   public OrderlyDisruptorEventFactory() {
   }

   public OrderlyDataEvent<T> newInstance() {
      return new OrderlyDataEvent<T>();
   }
}
