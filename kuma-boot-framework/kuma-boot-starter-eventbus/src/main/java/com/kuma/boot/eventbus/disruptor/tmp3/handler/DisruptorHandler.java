package com.kuma.boot.eventbus.disruptor.tmp3.handler;

import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;

public interface DisruptorHandler<T extends DisruptorEvent> {
   void doHandler(T event, HandlerChain<T> handlerChain) throws Exception;
}
