package com.kuma.boot.eventbus.disruptor.tmp3.handler;

import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;
import java.util.Map;
import java.util.Set;

public interface HandlerChainManager<T extends DisruptorEvent> {
   Map<String, DisruptorHandler<T>> getHandlers();

   NamedHandlerList<T> getChain(String chainName);

   boolean hasChains();

   Set<String> getChainNames();

   HandlerChain<T> proxy(HandlerChain<T> original, String chainName);

   void addHandler(String name, DisruptorHandler<T> handler);

   void createChain(String chainName, String chainDefinition);

   void addToChain(String chainName, String handlerName);
}
