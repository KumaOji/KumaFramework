package com.kuma.boot.eventbus.disruptor.tmp3.handler;

import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;

public abstract class AbstractNameableEventHandler<T extends DisruptorEvent> implements DisruptorHandler<T>, Nameable {
   protected String name;

   public AbstractNameableEventHandler() {
   }

   protected String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
