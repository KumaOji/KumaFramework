package com.kuma.boot.monitor.monitor.monitor.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class CacheMap<K, V> extends ConcurrentHashMap<K, V> {
   private Function<? super K, ? extends V> mappingFunction;

   public CacheMap(Function<? super K, ? extends V> mappingFunction) {
      this.mappingFunction = mappingFunction;
   }

   public V get(Object key) {
      V v = (V)super.get(key);
      if (v == null) {
         v = (V)this.computeIfAbsent(key, this.mappingFunction);
      }

      return v;
   }
}
