package com.kuma.boot.flowengine.easywork.context;

import com.kuma.boot.flowengine.easywork.util.Checker;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorkContext {
   private final Map<String, Object> contextMap = new ConcurrentHashMap();

   public WorkContext() {
   }

   public Map<String, Object> getContextMap() {
      return this.contextMap;
   }

   public WorkContext put(String key, Object value) {
      this.contextMap.put(key, value);
      return this;
   }

   public Object get(String key) {
      return this.contextMap.get(key);
   }

   public WorkContext remove(String key) {
      this.contextMap.remove(key);
      return this;
   }

   public WorkContext clear() {
      this.contextMap.clear();
      return this;
   }

   public void copy(WorkContext context) {
      if (Checker.BeEmpty(context.getContextMap())) {
         this.contextMap.putAll(context.contextMap);
      }

   }
}
