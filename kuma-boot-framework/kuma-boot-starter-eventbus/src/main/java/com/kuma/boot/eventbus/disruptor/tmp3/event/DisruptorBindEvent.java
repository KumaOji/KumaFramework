package com.kuma.boot.eventbus.disruptor.tmp3.event;

public class DisruptorBindEvent extends DisruptorEvent {
   protected Object bind;

   public DisruptorBindEvent() {
      super((Object)null);
   }

   public DisruptorBindEvent(Object source) {
      super(source);
   }

   public DisruptorBindEvent(Object source, Object bind) {
      super(source);
      this.bind = bind;
   }

   public Object getBind() {
      return this.bind;
   }

   public void bind(Object bind) {
      this.bind = bind;
   }
}
