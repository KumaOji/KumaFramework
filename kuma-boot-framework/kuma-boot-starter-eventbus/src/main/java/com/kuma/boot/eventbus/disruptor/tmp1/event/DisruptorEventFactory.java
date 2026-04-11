package com.kuma.boot.eventbus.disruptor.tmp1.event;

import com.lmax.disruptor.EventFactory;

public class DisruptorEventFactory<T> implements EventFactory<DataEvent<T>> {
   public DisruptorEventFactory() {
   }

   public DataEvent<T> newInstance() {
      return new DataEvent<T>();
   }
}
