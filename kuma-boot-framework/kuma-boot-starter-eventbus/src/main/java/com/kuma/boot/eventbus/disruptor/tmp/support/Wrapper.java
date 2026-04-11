package com.kuma.boot.eventbus.disruptor.tmp.support;

public interface Wrapper {
   default <T> T unwrap(Class<T> type) {
      if (this.isWrapperFor(type)) {
         return (T)type.cast(this.unwrap());
      } else {
         throw new ClassCastException("cannot be converted to " + String.valueOf(type));
      }
   }

   boolean isWrapperFor(Class<?> type);

   Object unwrap();
}
