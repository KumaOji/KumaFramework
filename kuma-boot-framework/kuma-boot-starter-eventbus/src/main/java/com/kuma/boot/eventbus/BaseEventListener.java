package com.kuma.boot.eventbus;

import com.lmax.disruptor.EventHandler;

public abstract class BaseEventListener<T> implements EventListener<T>, EventHandler<T> {
   public BaseEventListener() {
   }
}
