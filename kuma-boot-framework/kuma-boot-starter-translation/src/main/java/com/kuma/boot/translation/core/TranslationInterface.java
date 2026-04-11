package com.kuma.boot.translation.core;

public interface TranslationInterface<T> {
   T translation(Object key, String other);
}
