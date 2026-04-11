package com.kuma.boot.eventbus.disruptor.tmp5.service;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.eventbus.disruptor.tmp5.builder.CustomThreadBuilder;
import com.kuma.boot.eventbus.disruptor.tmp5.factory.CustomThreadFactory;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PrintThreadPoolService {
   private static final CustomThreadFactory customFactory = new CustomThreadFactory(CustomThreadBuilder.builder().name("PrintThreadPoolService-print-thread-pool-status").build());
   private static final ScheduledExecutorService printScheduledExecutorService;

   public PrintThreadPoolService() {
   }

   public static ScheduledExecutorService printThreadPoolStatus(ThreadPoolExecutor threadPool) {
      printScheduledExecutorService.scheduleAtFixedRate(() -> {
         LogUtils.info("===========printThreadPoolStatus==============", new Object[0]);
         LogUtils.info("ThreadPool Size: [{}]", new Object[]{threadPool.getPoolSize()});
         LogUtils.info("Active Threads: {}", new Object[]{threadPool.getActiveCount()});
         LogUtils.info("Number of Tasks: {}", new Object[]{threadPool.getCompletedTaskCount()});
         LogUtils.info("Number of Tasks in Queue: {}", new Object[]{threadPool.getQueue().size()});
         LogUtils.info("=========================", new Object[0]);
      }, 0L, 1L, TimeUnit.SECONDS);
      return printScheduledExecutorService;
   }

   static {
      printScheduledExecutorService = new ScheduledThreadPoolExecutor(1, customFactory);
   }
}
