package com.kuma.boot.eventbus.atlas.core;

public interface EventBus {
   void publish(Event event);

   void register(Object listener);

   void unregister(Object listener);

   void scanAndRegister(Object listener);
}
