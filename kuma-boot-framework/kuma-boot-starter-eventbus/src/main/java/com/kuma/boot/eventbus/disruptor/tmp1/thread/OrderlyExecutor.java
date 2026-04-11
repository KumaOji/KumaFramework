package com.kuma.boot.eventbus.disruptor.tmp1.thread;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import java.util.SortedMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class OrderlyExecutor extends ThreadPoolExecutor {
   private final ConcurrentSkipListMap<Long, SingletonExecutor> virtualExecutors = new ConcurrentSkipListMap();
   private final ThreadSelector threadSelector = new ThreadSelector();

   public OrderlyExecutor(final boolean isOrderly, final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue, final ThreadFactory threadFactory, final RejectedExecutionHandler handler) {
      super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
      this.orderlyThreadPool(isOrderly, corePoolSize, threadFactory);
   }

   private void orderlyThreadPool(final boolean isOrderly, final int corePoolSize, final ThreadFactory threadFactory) {
      if (isOrderly) {
         IntStream.range(0, corePoolSize).forEach((index) -> {
            SingletonExecutor singletonExecutor = new SingletonExecutor(threadFactory);
            int var10000 = singletonExecutor.hashCode();
            String hash = var10000 + ":" + index;
            byte[] bytes = this.threadSelector.sha(hash);

            for(int i = 0; i < 4; ++i) {
               this.virtualExecutors.put(this.threadSelector.hash(bytes, i), singletonExecutor);
            }

         });
      }

   }

   public SingletonExecutor select(final String hash) {
      long select = this.threadSelector.select(hash);
      if (!this.virtualExecutors.containsKey(select)) {
         SortedMap<Long, SingletonExecutor> tailMap = this.virtualExecutors.tailMap(select);
         if (tailMap.isEmpty()) {
            select = (Long)this.virtualExecutors.firstKey();
         } else {
            select = (Long)tailMap.firstKey();
         }
      }

      return (SingletonExecutor)this.virtualExecutors.get(select);
   }

   private static final class ThreadSelector {
      private ThreadSelector() {
      }

      public long select(final String hash) {
         byte[] digest = this.sha(hash);
         return this.hash(digest, 0);
      }

      private long hash(final byte[] digest, final int number) {
         return ((long)(digest[3 + number * 4] & 255) << 24 | (long)(digest[2 + number * 4] & 255) << 16 | (long)(digest[1 + number * 4] & 255) << 8 | (long)(digest[number * 4] & 255)) & 4294967295L;
      }

      private byte[] sha(final String hash) {
         byte[] bytes = hash.getBytes(StandardCharsets.UTF_8);
         return Hashing.sha256().newHasher().putBytes(bytes).hash().asBytes();
      }
   }
}
