package com.kuma.boot.eventbus.disruptor.tmp4.listener;

import com.kuma.boot.eventbus.disruptor.tmp4.event.EventContext;

public interface WorkerEventListener<T extends EventContext> {
   void onEvent(T context);
}
