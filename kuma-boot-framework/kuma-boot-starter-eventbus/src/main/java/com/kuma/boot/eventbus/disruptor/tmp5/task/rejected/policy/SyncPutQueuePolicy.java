package com.kuma.boot.eventbus.disruptor.tmp5.task.rejected.policy;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class SyncPutQueuePolicy implements RejectedExecutionHandler {
   public SyncPutQueuePolicy() {
   }

   public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
      if (!executor.isShutdown()) {
         try {
            executor.getQueue().put(r);
         } catch (InterruptedException e) {
            LogUtils.error("Adding Queue task to thread pool failed.", new Object[]{e});
         }

      }
   }
}
