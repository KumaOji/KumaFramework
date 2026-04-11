package com.kuma.boot.eventbus.disruptor.tmp3.handler;

import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;

public interface PathProcessor<T extends DisruptorEvent> {
   DisruptorHandler<T> processPath(String path);
}
