package com.kuma.boot.canal.model;

import com.kuma.boot.canal.annotation.ListenPoint;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ListenerPoint {
   private Object target;
   private Map<Method, ListenPoint> invokeMap = new HashMap();

   public ListenerPoint(Object target, Method method, ListenPoint anno) {
      this.target = target;
      this.invokeMap.put(method, anno);
   }

   public Object getTarget() {
      return this.target;
   }

   public Map<Method, ListenPoint> getInvokeMap() {
      return this.invokeMap;
   }
}
