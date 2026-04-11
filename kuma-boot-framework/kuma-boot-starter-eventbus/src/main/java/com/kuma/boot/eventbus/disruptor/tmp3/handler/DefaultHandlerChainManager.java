package com.kuma.boot.eventbus.disruptor.tmp3.handler;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;
import com.kuma.boot.eventbus.disruptor.tmp3.util.StringUtils;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.util.CollectionUtils;

public class DefaultHandlerChainManager implements HandlerChainManager<DisruptorEvent> {
   private Map<String, DisruptorHandler<DisruptorEvent>> handlers = new LinkedHashMap();
   private Map<String, NamedHandlerList<DisruptorEvent>> handlerChains = new LinkedHashMap();
   private static final String DEFAULT_CHAIN_DEFINATION_DELIMITER_CHAR = ",";

   public DefaultHandlerChainManager() {
   }

   public Map<String, DisruptorHandler<DisruptorEvent>> getHandlers() {
      return this.handlers;
   }

   public void setHandlers(Map<String, DisruptorHandler<DisruptorEvent>> handlers) {
      this.handlers = handlers;
   }

   public Map<String, NamedHandlerList<DisruptorEvent>> getHandlerChains() {
      return this.handlerChains;
   }

   public void setHandlerChains(Map<String, NamedHandlerList<DisruptorEvent>> handlerChains) {
      this.handlerChains = handlerChains;
   }

   public DisruptorHandler<DisruptorEvent> getHandler(String name) {
      return (DisruptorHandler)this.handlers.get(name);
   }

   public void addHandler(String name, DisruptorHandler<DisruptorEvent> handler) {
      this.addHandler(name, handler, true);
   }

   protected void addHandler(String name, DisruptorHandler<DisruptorEvent> handler, boolean overwrite) {
      DisruptorHandler<DisruptorEvent> existing = this.getHandler(name);
      if (existing == null || overwrite) {
         if (handler instanceof Nameable) {
            ((Nameable)handler).setName(name);
         }

         this.handlers.put(name, handler);
      }

   }

   public void createChain(String chainName, String chainDefinition) {
      if (StringUtils.isBlank(chainName)) {
         throw new NullPointerException("chainName cannot be null or empty.");
      } else if (StringUtils.isBlank(chainDefinition)) {
         throw new NullPointerException("chainDefinition cannot be null or empty.");
      } else {
         LogUtils.debug("Creating chain [" + chainName + "] from String definition [" + chainDefinition + "]", new Object[0]);
         String[] handlerTokens = this.splitChainDefinition(chainDefinition);

         for(String token : handlerTokens) {
            this.addToChain(chainName, token);
         }

      }
   }

   protected String[] splitChainDefinition(String chainDefinition) {
      String trimToNull = StringUtils.trimToNull(chainDefinition);
      if (trimToNull == null) {
         return null;
      } else {
         String[] split = StringUtils.splits(trimToNull, ",");

         for(int i = 0; i < split.length; ++i) {
            split[i] = StringUtils.trimToNull(split[i]);
         }

         return split;
      }
   }

   public void addToChain(String chainName, String handlerName) {
      if (StringUtils.isBlank(chainName)) {
         throw new IllegalArgumentException("chainName cannot be null or empty.");
      } else {
         DisruptorHandler<DisruptorEvent> handler = this.getHandler(handlerName);
         if (handler == null) {
            throw new IllegalArgumentException("There is no handler with name '" + handlerName + "' to apply to chain [" + chainName + "] in the pool of available Handlers.  Ensure a handler with that name/path has first been registered with the addHandler method(s).");
         } else {
            NamedHandlerList<DisruptorEvent> chain = this.ensureChain(chainName);
            chain.add(handler);
         }
      }
   }

   protected NamedHandlerList<DisruptorEvent> ensureChain(String chainName) {
      NamedHandlerList<DisruptorEvent> chain = this.getChain(chainName);
      if (chain == null) {
         chain = new DefaultNamedHandlerList(chainName);
         this.handlerChains.put(chainName, chain);
      }

      return chain;
   }

   public NamedHandlerList<DisruptorEvent> getChain(String chainName) {
      return (NamedHandlerList)this.handlerChains.get(chainName);
   }

   public boolean hasChains() {
      return !CollectionUtils.isEmpty(this.handlerChains);
   }

   public Set<String> getChainNames() {
      return this.handlerChains != null ? this.handlerChains.keySet() : Collections.EMPTY_SET;
   }

   public HandlerChain<DisruptorEvent> proxy(HandlerChain<DisruptorEvent> original, String chainName) {
      NamedHandlerList<DisruptorEvent> configured = this.getChain(chainName);
      if (configured == null) {
         String msg = "There is no configured chain under the name/key [" + chainName + "].";
         throw new IllegalArgumentException(msg);
      } else {
         return configured.proxy(original);
      }
   }
}
