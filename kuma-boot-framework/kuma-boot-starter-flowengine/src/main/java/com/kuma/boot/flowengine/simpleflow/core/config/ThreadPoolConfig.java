package com.kuma.boot.flowengine.simpleflow.core.config;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolConfig {
   private int coreSize = 4;
   private int maxSize = 8;
   private int keepAliveSeconds = 60;
   private int queueCapacity = 100;
   private String threadNamePrefix = "simple-flow-";

   public ThreadPoolConfig() {
   }

   public static ThreadPoolConfig parseFromConfig(Map<String, Object> config) {
      ThreadPoolConfig threadPoolConfig = new ThreadPoolConfig();
      if (config == null) {
         LogUtils.info("\u672a\u627e\u5230\u7ebf\u7a0b\u6c60\u914d\u7f6e\uff0c\u4f7f\u7528\u9ed8\u8ba4\u914d\u7f6e", new Object[0]);
         return threadPoolConfig;
      } else {
         Map<String, Object> threadPoolMap = (Map)config.get("thread-pool");
         if (threadPoolMap == null) {
            LogUtils.info("\u672a\u627e\u5230thread-pool\u914d\u7f6e\uff0c\u4f7f\u7528\u9ed8\u8ba4\u914d\u7f6e", new Object[0]);
            return threadPoolConfig;
         } else {
            Object coreSizeObj = threadPoolMap.get("core-size");
            if (coreSizeObj instanceof Number) {
               threadPoolConfig.setCoreSize(((Number)coreSizeObj).intValue());
            }

            Object maxSizeObj = threadPoolMap.get("max-size");
            if (maxSizeObj instanceof Number) {
               threadPoolConfig.setMaxSize(((Number)maxSizeObj).intValue());
            }

            Object keepAliveObj = threadPoolMap.get("keep-alive-seconds");
            if (keepAliveObj instanceof Number) {
               threadPoolConfig.setKeepAliveSeconds(((Number)keepAliveObj).intValue());
            }

            Object queueCapacityObj = threadPoolMap.get("queue-capacity");
            if (queueCapacityObj instanceof Number) {
               threadPoolConfig.setQueueCapacity(((Number)queueCapacityObj).intValue());
            }

            Object threadNamePrefixObj = threadPoolMap.get("thread-name-prefix");
            if (threadNamePrefixObj instanceof String) {
               threadPoolConfig.setThreadNamePrefix((String)threadNamePrefixObj);
            }

            LogUtils.info("\u89e3\u6790\u7ebf\u7a0b\u6c60\u914d\u7f6e: coreSize={}, maxSize={}, keepAliveSeconds={}, queueCapacity={}, threadNamePrefix={}", new Object[]{threadPoolConfig.getCoreSize(), threadPoolConfig.getMaxSize(), threadPoolConfig.getKeepAliveSeconds(), threadPoolConfig.getQueueCapacity(), threadPoolConfig.getThreadNamePrefix()});
            return threadPoolConfig;
         }
      }
   }

   public int getCoreSize() {
      return this.coreSize;
   }

   public void setCoreSize(int coreSize) {
      this.coreSize = coreSize;
   }

   public int getMaxSize() {
      return this.maxSize;
   }

   public void setMaxSize(int maxSize) {
      this.maxSize = maxSize;
   }

   public int getKeepAliveSeconds() {
      return this.keepAliveSeconds;
   }

   public void setKeepAliveSeconds(int keepAliveSeconds) {
      this.keepAliveSeconds = keepAliveSeconds;
   }

   public int getQueueCapacity() {
      return this.queueCapacity;
   }

   public void setQueueCapacity(int queueCapacity) {
      this.queueCapacity = queueCapacity;
   }

   public String getThreadNamePrefix() {
      return this.threadNamePrefix;
   }

   public void setThreadNamePrefix(String threadNamePrefix) {
      this.threadNamePrefix = threadNamePrefix;
   }

   public ThreadPoolExecutor createThreadPoolExecutor() {
      ThreadPoolExecutor executor = new ThreadPoolExecutor(this.coreSize, this.maxSize, (long)this.keepAliveSeconds, TimeUnit.SECONDS, new ArrayBlockingQueue(this.queueCapacity), (r) -> {
         Thread thread = new Thread(r);
         String var10001 = this.threadNamePrefix;
         thread.setName(var10001 + thread.getId());
         thread.setDaemon(false);
         return thread;
      }, new ThreadPoolExecutor.CallerRunsPolicy());
      LogUtils.info("\u521b\u5efa\u7ebf\u7a0b\u6c60\u6267\u884c\u5668: coreSize={}, maxSize={}, keepAliveSeconds={}, queueCapacity={}", new Object[]{this.coreSize, this.maxSize, this.keepAliveSeconds, this.queueCapacity});
      return executor;
   }
}
