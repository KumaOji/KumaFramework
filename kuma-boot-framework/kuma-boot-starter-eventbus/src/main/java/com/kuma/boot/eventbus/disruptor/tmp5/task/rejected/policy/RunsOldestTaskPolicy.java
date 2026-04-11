package com.kuma.boot.eventbus.disruptor.tmp5.task.rejected.policy;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class RunsOldestTaskPolicy implements RejectedExecutionHandler {
   public RunsOldestTaskPolicy() {
   }

   public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
      if (!executor.isShutdown()) {
         BlockingQueue<Runnable> workQueue = executor.getQueue();
         Runnable firstWork = (Runnable)workQueue.poll();
         boolean newTaskAdd = workQueue.offer(r);
         if (firstWork != null) {
            firstWork.run();
         }

         if (!newTaskAdd) {
            executor.execute(r);
         }

      }
   }
}
