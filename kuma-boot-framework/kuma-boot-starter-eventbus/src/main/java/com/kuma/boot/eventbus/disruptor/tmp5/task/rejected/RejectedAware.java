package com.kuma.boot.eventbus.disruptor.tmp5.task.rejected;

import java.util.concurrent.ThreadPoolExecutor;

public interface RejectedAware {
   default void beforeReject(ThreadPoolExecutor executor) {
   }
}
