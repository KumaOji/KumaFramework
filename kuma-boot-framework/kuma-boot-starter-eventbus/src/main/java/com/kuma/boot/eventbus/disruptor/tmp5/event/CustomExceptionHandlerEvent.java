package com.kuma.boot.eventbus.disruptor.tmp5.event;

import org.springframework.context.ApplicationEvent;

public class CustomExceptionHandlerEvent extends ApplicationEvent {
   private Throwable ex;
   private long sequence;
   private DisruptorEvent event;

   public CustomExceptionHandlerEvent(Object source, Throwable ex, long sequence, DisruptorEvent event) {
      super(source);
      this.ex = ex;
      this.sequence = sequence;
      this.event = event;
   }

   public Throwable getEx() {
      return this.ex;
   }

   public long getSequence() {
      return this.sequence;
   }

   public DisruptorEvent getEvent() {
      return this.event;
   }
}
