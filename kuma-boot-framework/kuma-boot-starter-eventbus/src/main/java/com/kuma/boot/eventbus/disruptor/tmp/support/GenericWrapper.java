package com.kuma.boot.eventbus.disruptor.tmp.support;

import org.springframework.core.GenericTypeResolver;

public interface GenericWrapper<T> extends Wrapper {
   static <T> GenericWrapper<T> of(T delegate) {
      return new RecordWrapper<T>(delegate);
   }

   default boolean isWrapperFor(Class<?> type) {
      Class<?> typeArgument = GenericTypeResolver.resolveTypeArgument(this.getClass(), GenericWrapper.class);
      return typeArgument == null || type.isAssignableFrom(typeArgument);
   }

   T unwrap();
}
