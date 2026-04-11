package com.kuma.boot.eventbus.disruptor.tmp1.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SingletonExecutor extends ThreadPoolExecutor {
   public SingletonExecutor(final ThreadFactory factory) {
      super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), factory);
   }
}
