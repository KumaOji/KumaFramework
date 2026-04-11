package com.kuma.boot.eventbus.disruptor.tmp3.handler;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class PathMatchingHandlerChainResolver implements HandlerChainResolver<DisruptorEvent> {
   private HandlerChainManager<DisruptorEvent> handlerChainManager = new DefaultHandlerChainManager();
   private PathMatcher pathMatcher = new AntPathMatcher();

   public PathMatchingHandlerChainResolver() {
   }

   public HandlerChainManager<DisruptorEvent> getHandlerChainManager() {
      return this.handlerChainManager;
   }

   public void setHandlerChainManager(HandlerChainManager<DisruptorEvent> handlerChainManager) {
      this.handlerChainManager = handlerChainManager;
   }

   public PathMatcher getPathMatcher() {
      return this.pathMatcher;
   }

   public void setPathMatcher(PathMatcher pathMatcher) {
      this.pathMatcher = pathMatcher;
   }

   public HandlerChain<DisruptorEvent> getChain(DisruptorEvent event, HandlerChain<DisruptorEvent> originalChain) {
      HandlerChainManager<DisruptorEvent> handlerChainManager = this.getHandlerChainManager();
      if (!handlerChainManager.hasChains()) {
         return null;
      } else {
         String eventURI = this.getPathWithinEvent(event);

         for(String pathPattern : handlerChainManager.getChainNames()) {
            if (this.pathMatches(pathPattern, eventURI)) {
               LogUtils.info("Matched path pattern [" + pathPattern + "] for eventURI [" + eventURI + "].  Utilizing corresponding handler chain...", new Object[0]);
               return handlerChainManager.proxy(originalChain, pathPattern);
            }
         }

         return null;
      }
   }

   protected boolean pathMatches(String pattern, String path) {
      PathMatcher pathMatcher = this.getPathMatcher();
      return pathMatcher.match(pattern, path);
   }

   protected String getPathWithinEvent(DisruptorEvent event) {
      return event.getRouteExpression();
   }
}
