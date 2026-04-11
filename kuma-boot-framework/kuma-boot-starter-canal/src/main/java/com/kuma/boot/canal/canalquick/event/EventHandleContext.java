package com.kuma.boot.canal.canalquick.event;

public class EventHandleContext {
   private IEventHandler handler;
   private EventInfo eventInfo;

   public EventHandleContext(IEventHandler handler, EventInfo eventInfo) {
      this.handler = handler;
      this.eventInfo = eventInfo;
   }

   public void setHandler(IEventHandler handler) {
      this.handler = handler;
   }

   public void handle() {
      this.handler.handle(this.eventInfo);
   }
}
