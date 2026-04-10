package org.slf4j;

import com.alibaba.ttl.TransmittableThreadLocal;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.spi.MDCAdapter;

public class TtlMDCAdapter implements MDCAdapter {
   private final ThreadLocal<Map<String, String>> copyOnInheritThreadLocal = new TransmittableThreadLocal();
   private static final int WRITE_OPERATION = 1;
   private static final int MAP_COPY_OPERATION = 2;
   private static final TtlMDCAdapter mtcMDCAdapter = new TtlMDCAdapter();
   private final ThreadLocal<Integer> lastOperation = new ThreadLocal();

   public TtlMDCAdapter() {
   }

   public static MDCAdapter getInstance() {
      return mtcMDCAdapter;
   }

   private Integer getAndSetLastOperation(int op) {
      Integer lastOp = (Integer)this.lastOperation.get();
      this.lastOperation.set(op);
      return lastOp;
   }

   private static boolean wasLastOpReadOrNull(Integer lastOp) {
      return lastOp == null || lastOp == 2;
   }

   private Map<String, String> duplicateAndInsertNewMap(Map<String, String> oldMap) {
      Map<String, String> newMap = Collections.synchronizedMap(new HashMap());
      if (oldMap != null) {
         synchronized(oldMap) {
            newMap.putAll(oldMap);
         }
      }

      this.copyOnInheritThreadLocal.set(newMap);
      return newMap;
   }

   public void put(String key, String val) {
      if (key == null) {
         throw new IllegalArgumentException("key cannot be null");
      } else {
         Map<String, String> oldMap = (Map)this.copyOnInheritThreadLocal.get();
         Integer lastOp = this.getAndSetLastOperation(1);
         if (!wasLastOpReadOrNull(lastOp) && oldMap != null) {
            oldMap.put(key, val);
         } else {
            Map<String, String> newMap = this.duplicateAndInsertNewMap(oldMap);
            newMap.put(key, val);
         }

      }
   }

   public void remove(String key) {
      if (key != null) {
         Map<String, String> oldMap = (Map)this.copyOnInheritThreadLocal.get();
         if (oldMap != null) {
            Integer lastOp = this.getAndSetLastOperation(1);
            if (wasLastOpReadOrNull(lastOp)) {
               Map<String, String> newMap = this.duplicateAndInsertNewMap(oldMap);
               newMap.remove(key);
            } else {
               oldMap.remove(key);
            }

         }
      }
   }

   public void clear() {
      this.lastOperation.set(1);
      this.copyOnInheritThreadLocal.remove();
   }

   public String get(String key) {
      Map<String, String> map = (Map)this.copyOnInheritThreadLocal.get();
      return map != null && key != null ? (String)map.get(key) : null;
   }

   public Map<String, String> getPropertyMap() {
      this.lastOperation.set(2);
      return (Map)this.copyOnInheritThreadLocal.get();
   }

   public Set<String> getKeys() {
      Map<String, String> map = this.getPropertyMap();
      return map != null ? map.keySet() : null;
   }

   public Map<String, String> getCopyOfContextMap() {
      Map<String, String> hashMap = (Map)this.copyOnInheritThreadLocal.get();
      return hashMap == null ? null : new HashMap(hashMap);
   }

   public void setContextMap(Map<String, String> contextMap) {
      this.lastOperation.set(1);
      Map<String, String> newMap = Collections.synchronizedMap(new HashMap());
      newMap.putAll(contextMap);
      this.copyOnInheritThreadLocal.set(newMap);
   }

   public void pushByKey(String key, String value) {
   }

   public String popByKey(String key) {
      return null;
   }

   public Deque<String> getCopyOfDequeByKey(String key) {
      return null;
   }

   public void clearDequeByKey(String key) {
   }

   static {
      MDC.MDC_ADAPTER = mtcMDCAdapter;
   }
}
