package com.kuma.boot.eventbus.disruptor.tmp5.handler;

import com.lmax.disruptor.dsl.Disruptor;

public interface DisruptorHandler {
   void buildHandler(String key, Disruptor disruptor);
}
