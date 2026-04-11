package com.kuma.boot.eventbus.disruptor;

import com.lmax.disruptor.EventFactory;
import com.kuma.boot.eventbus.model.EventModel;

public class EventModelFactory<T> implements EventFactory<EventModel<T>> {
   public EventModelFactory() {
   }

   public EventModel<T> newInstance() {
      return new EventModel<T>();
   }
}
