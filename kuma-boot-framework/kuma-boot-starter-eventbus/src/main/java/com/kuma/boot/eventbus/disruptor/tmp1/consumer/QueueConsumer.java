package com.kuma.boot.eventbus.disruptor.tmp1.consumer;

import com.lmax.disruptor.EventHandler;
import com.kuma.boot.eventbus.disruptor.tmp1.event.DataEvent;
import com.kuma.boot.eventbus.disruptor.tmp1.event.OrderlyDataEvent;
import com.kuma.boot.eventbus.disruptor.tmp1.thread.OrderlyExecutor;
import java.util.concurrent.ThreadPoolExecutor;

public class QueueConsumer<T> implements EventHandler<DataEvent<T>> {
   private final OrderlyExecutor executor;
   private final QueueConsumerFactory<T> factory;

   public QueueConsumer(final OrderlyExecutor executor, final QueueConsumerFactory<T> factory) {
      this.executor = executor;
      this.factory = factory;
   }

   private ThreadPoolExecutor orderly(final DataEvent<T> t) {
      return (ThreadPoolExecutor)(t instanceof OrderlyDataEvent && !this.isEmpty(((OrderlyDataEvent)t).getHash()) ? this.executor.select(((OrderlyDataEvent)t).getHash()) : this.executor);
   }

   private boolean isEmpty(final String t) {
      return t == null || t.isEmpty();
   }

   public void onEvent(DataEvent<T> t, long l, boolean b) throws Exception {
      if (t != null) {
         ThreadPoolExecutor executor = this.orderly(t);
         QueueConsumerExecutor<T> queueConsumerExecutor = this.factory.create();
         queueConsumerExecutor.setData(t.getData());
         t.setData((Object)null);
         executor.execute(queueConsumerExecutor);
      }

   }
}
