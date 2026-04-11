package com.kuma.boot.flowengine.module;

import com.google.common.collect.Lists;
import java.util.List;

public class EventListeners {
   private List<EventListener> listeners = Lists.newArrayList();

   public EventListeners() {
   }

   public void addListener(EventListener listener) {
      this.listeners.add(listener);
   }

   public List<EventListener> listeners() {
      return this.listeners;
   }
}
