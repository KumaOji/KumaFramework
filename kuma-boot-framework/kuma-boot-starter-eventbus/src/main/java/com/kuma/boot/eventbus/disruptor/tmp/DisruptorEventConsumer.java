package com.kuma.boot.eventbus.disruptor.tmp;

import com.lmax.disruptor.EventHandler;

public interface DisruptorEventConsumer<T> extends EventHandler<T> {
}
