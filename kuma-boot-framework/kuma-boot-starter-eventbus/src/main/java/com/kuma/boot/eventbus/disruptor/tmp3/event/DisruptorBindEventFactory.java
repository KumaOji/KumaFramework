package com.kuma.boot.eventbus.disruptor.tmp3.event;

import com.lmax.disruptor.EventFactory;

public class DisruptorBindEventFactory implements EventFactory<DisruptorEvent> {
   public DisruptorBindEventFactory() {
   }

   public DisruptorEvent newInstance() {
      return new DisruptorBindEvent(this);
   }
}
