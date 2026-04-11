package com.kuma.boot.eventbus.disruptor.tmp3.handler;

import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;

public interface HandlerChainResolver<T extends DisruptorEvent> {
   HandlerChain<T> getChain(T event, HandlerChain<T> originalChain);
}
