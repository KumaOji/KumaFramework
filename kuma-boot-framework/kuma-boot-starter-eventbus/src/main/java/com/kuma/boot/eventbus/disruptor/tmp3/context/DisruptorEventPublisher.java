package com.kuma.boot.eventbus.disruptor.tmp3.context;

import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;

public interface DisruptorEventPublisher {
   void publishEvent(DisruptorEvent event);
}
