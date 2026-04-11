package com.kuma.boot.eventbus.disruptor.tmp3.handler;

import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;
import java.util.List;

public interface NamedHandlerList<T extends DisruptorEvent> extends List<DisruptorHandler<T>> {
   String getName();

   HandlerChain<T> proxy(HandlerChain<T> handlerChain);
}
