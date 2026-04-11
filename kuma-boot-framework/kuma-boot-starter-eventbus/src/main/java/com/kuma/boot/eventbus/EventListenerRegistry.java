package com.kuma.boot.eventbus;

import java.util.List;

public interface EventListenerRegistry<P> {
   void initRegistryEventListener(List<EventListener> eventConsumerList);

   void publish(P param);
}
