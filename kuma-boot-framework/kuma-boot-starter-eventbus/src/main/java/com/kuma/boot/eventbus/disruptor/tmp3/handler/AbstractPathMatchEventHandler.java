package com.kuma.boot.eventbus.disruptor.tmp3.handler;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public abstract class AbstractPathMatchEventHandler<T extends DisruptorEvent> extends AbstractAdviceEventHandler<T> implements PathProcessor<T> {
   protected PathMatcher pathMatcher = new AntPathMatcher();
   protected List<String> appliedPaths = new ArrayList();

   public AbstractPathMatchEventHandler() {
   }

   public DisruptorHandler<T> processPath(String path) {
      this.appliedPaths.add(path);
      return this;
   }

   protected String getPathWithinEvent(T event) {
      return event.getRouteExpression();
   }

   protected boolean pathsMatch(String path, T event) {
      String eventExp = this.getPathWithinEvent(event);
      LogUtils.info("Attempting to match pattern '{}' with current Event Expression '{}'...", new Object[]{path, eventExp});
      return this.pathsMatch(path, eventExp);
   }

   protected boolean pathsMatch(String pattern, String path) {
      return this.pathMatcher.match(pattern, path);
   }

   protected boolean preHandle(T event) throws Exception {
      if (this.appliedPaths != null && !this.appliedPaths.isEmpty()) {
         for(String path : this.appliedPaths) {
            if (this.pathsMatch(path, event)) {
               LogUtils.info("Current Event Expression matches pattern '{}'.  Determining handler chain execution...", new Object[]{path});
               return this.isHandlerChainContinued(event, path);
            }
         }

         return true;
      } else {
         LogUtils.info("appliedPaths property is null or empty.  This Handler will passthrough immediately.", new Object[0]);
         return true;
      }
   }

   private boolean isHandlerChainContinued(T event, String path) throws Exception {
      if (this.isEnabled(event, path)) {
         LogUtils.info("Handler '{}' is enabled for the current event under path '{}'.  Delegating to subclass implementation for 'onPreHandle' check.", new Object[]{this.getName(), path});
         return this.onPreHandle(event);
      } else {
         LogUtils.info("Handler '{}' is disabled for the current event under path '{}'.  The next element in the HandlerChain will be called immediately.", new Object[]{this.getName(), path});
         return true;
      }
   }

   protected boolean onPreHandle(T event) throws Exception {
      return true;
   }

   protected boolean isEnabled(T event, String path) throws Exception {
      return this.isEnabled(event);
   }

   public PathMatcher getPathMatcher() {
      return this.pathMatcher;
   }

   public void setPathMatcher(PathMatcher pathMatcher) {
      this.pathMatcher = pathMatcher;
   }

   public List<String> getAppliedPaths() {
      return this.appliedPaths;
   }
}
