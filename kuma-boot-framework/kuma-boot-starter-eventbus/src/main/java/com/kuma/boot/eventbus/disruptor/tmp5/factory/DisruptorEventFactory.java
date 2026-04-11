package com.kuma.boot.eventbus.disruptor.tmp5.factory;

import com.lmax.disruptor.EventFactory;
import com.kuma.boot.eventbus.disruptor.tmp5.event.DisruptorEvent;

public class DisruptorEventFactory implements EventFactory<DisruptorEvent> {
   public DisruptorEventFactory() {
   }

   public DisruptorEvent newInstance() {
      return new DisruptorEvent();
   }
}
