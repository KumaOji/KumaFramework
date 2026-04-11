package com.kuma.boot.eventbus;

public interface EventListener<T> {
   String topic();

   void onMessage(T message);
}
