package com.kuma.boot.eventbus.disruptor.tmp3.event;

import org.springframework.context.ApplicationEvent;

public class DisruptorApplicationEvent extends ApplicationEvent {
   protected Object bind;

   public DisruptorApplicationEvent(Object source, Object bind) {
      super(source);
      this.bind = bind;
   }

   public DisruptorApplicationEvent(Object source) {
      super(source);
   }

   public Object getBind() {
      return this.bind;
   }

   public void bind(Object bind) {
      this.bind = bind;
   }
}
