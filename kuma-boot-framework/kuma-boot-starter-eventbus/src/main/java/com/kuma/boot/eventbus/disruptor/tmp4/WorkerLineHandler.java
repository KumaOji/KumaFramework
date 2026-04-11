package com.kuma.boot.eventbus.disruptor.tmp4;

import com.lmax.disruptor.EventHandler;
import com.kuma.boot.eventbus.disruptor.tmp4.event.WorkEvent;
import com.kuma.boot.eventbus.disruptor.tmp4.thread.SafeThreadPool;
import java.util.concurrent.CountDownLatch;

public class WorkerLineHandler implements EventHandler<WorkEvent> {
   private final SafeThreadPool threadPool;

   public WorkerLineHandler(SafeThreadPool threadPool) {
      this.threadPool = threadPool;
   }

   public void onEvent(WorkEvent event, long l, boolean b) throws Exception {
      CountDownLatch latch = new CountDownLatch(event.getListeners().size());

      try {
         event.getListeners().forEach((listener) -> this.threadPool.submit(() -> {
               try {
                  listener.onEvent(event.getContext());
               } finally {
                  latch.countDown();
               }

            }));
         latch.await();
      } finally {
         event.finished();
      }

   }
}
