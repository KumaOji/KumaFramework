package com.kuma.boot.translation.translationjackson.core;

public interface Translation<T> {
   default void init(Object result) {
   }

   T translation(String key, String readConverterExp, Object value);
}
