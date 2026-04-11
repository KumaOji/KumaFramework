package com.kuma.boot.eventbus.disruptor.tmp1.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public final class DisruptorThreadFactory implements ThreadFactory {
   private static final AtomicLong THREAD_NUMBER = new AtomicLong(1L);
   private static final ThreadGroup THREAD_GROUP = new ThreadGroup("kmc-disruptor");
   private final boolean daemon;
   private final String namePrefix;
   private final int priority;

   private DisruptorThreadFactory(final String namePrefix, final boolean daemon, final int priority) {
      this.namePrefix = namePrefix;
      this.daemon = daemon;
      this.priority = priority;
   }

   public static ThreadFactory create(final String namePrefix, final boolean daemon) {
      return create(namePrefix, daemon, 5);
   }

   public static ThreadFactory create(final String namePrefix, final boolean daemon, final int priority) {
      return new DisruptorThreadFactory(namePrefix, daemon, priority);
   }

   public Thread newThread(final Runnable runnable) {
      ThreadGroup var10002 = THREAD_GROUP;
      String var10004 = THREAD_GROUP.getName();
      Thread thread = new Thread(var10002, runnable, var10004 + "-" + this.namePrefix + "-" + THREAD_NUMBER.getAndIncrement());
      thread.setDaemon(this.daemon);
      thread.setPriority(this.priority);
      return thread;
   }
}
