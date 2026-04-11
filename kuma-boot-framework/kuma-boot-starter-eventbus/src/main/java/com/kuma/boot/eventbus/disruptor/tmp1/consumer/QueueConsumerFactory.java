package com.kuma.boot.eventbus.disruptor.tmp1.consumer;

public interface QueueConsumerFactory<T> {
   QueueConsumerExecutor<T> create();

   String fixName();
}
