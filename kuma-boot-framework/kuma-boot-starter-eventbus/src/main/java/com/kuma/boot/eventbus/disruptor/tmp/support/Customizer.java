package com.kuma.boot.eventbus.disruptor.tmp.support;

@FunctionalInterface
public interface Customizer<T> {
   void customize(T t);
}
