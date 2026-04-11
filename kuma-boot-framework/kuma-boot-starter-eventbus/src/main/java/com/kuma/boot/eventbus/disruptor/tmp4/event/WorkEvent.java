package com.kuma.boot.eventbus.disruptor.tmp4.event;

import com.kuma.boot.eventbus.disruptor.tmp4.listener.WorkerEventListener;
import java.util.Set;

public class WorkEvent extends AbstractEvent {
   private EventContext context;
   private Set<WorkerEventListener> listeners;

   public WorkEvent() {
   }

   public EventContext getContext() {
      return this.context;
   }

   public void setContext(EventContext context) {
      this.context = context;
   }

   public Set<WorkerEventListener> getListeners() {
      return this.listeners;
   }

   public void setListeners(Set<WorkerEventListener> listeners) {
      this.listeners = listeners;
   }
}
