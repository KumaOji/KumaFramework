package com.kuma.boot.flowengine.simpleflow.core.config;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Map;

public class ExecutionConfig {
   private long defaultTimeoutMs = 30000L;
   private int defaultRetryCount = 3;
   private long defaultRetryDelayMs = 1000L;
   private boolean parallelEnabled = true;
   private int maxParallelism = 4;

   public ExecutionConfig() {
   }

   public long getDefaultTimeoutMs() {
      return this.defaultTimeoutMs;
   }

   public void setDefaultTimeoutMs(long defaultTimeoutMs) {
      this.defaultTimeoutMs = defaultTimeoutMs;
   }

   public int getDefaultRetryCount() {
      return this.defaultRetryCount;
   }

   public void setDefaultRetryCount(int defaultRetryCount) {
      this.defaultRetryCount = defaultRetryCount;
   }

   public long getDefaultRetryDelayMs() {
      return this.defaultRetryDelayMs;
   }

   public void setDefaultRetryDelayMs(long defaultRetryDelayMs) {
      this.defaultRetryDelayMs = defaultRetryDelayMs;
   }

   public boolean isParallelEnabled() {
      return this.parallelEnabled;
   }

   public void setParallelEnabled(boolean parallelEnabled) {
      this.parallelEnabled = parallelEnabled;
   }

   public int getMaxParallelism() {
      return this.maxParallelism;
   }

   public void setMaxParallelism(int maxParallelism) {
      this.maxParallelism = maxParallelism;
   }

   public static ExecutionConfig parseFromConfig(Map<String, Object> config) {
      ExecutionConfig executionConfig = new ExecutionConfig();
      if (config == null) {
         LogUtils.info("\u672a\u627e\u5230\u6267\u884c\u914d\u7f6e\uff0c\u4f7f\u7528\u9ed8\u8ba4\u914d\u7f6e", new Object[0]);
         return executionConfig;
      } else {
         Map<String, Object> executionMap = (Map)config.get("execution");
         if (executionMap == null) {
            LogUtils.info("\u672a\u627e\u5230execution\u914d\u7f6e\uff0c\u4f7f\u7528\u9ed8\u8ba4\u914d\u7f6e", new Object[0]);
            return executionConfig;
         } else {
            Object defaultTimeoutObj = executionMap.get("default-timeout-ms");
            if (defaultTimeoutObj instanceof Number) {
               executionConfig.setDefaultTimeoutMs(((Number)defaultTimeoutObj).longValue());
            }

            Object defaultRetryCountObj = executionMap.get("default-retry-count");
            if (defaultRetryCountObj instanceof Number) {
               executionConfig.setDefaultRetryCount(((Number)defaultRetryCountObj).intValue());
            }

            Object defaultRetryDelayObj = executionMap.get("default-retry-delay-ms");
            if (defaultRetryDelayObj instanceof Number) {
               executionConfig.setDefaultRetryDelayMs(((Number)defaultRetryDelayObj).longValue());
            }

            Object parallelEnabledObj = executionMap.get("parallel-enabled");
            if (parallelEnabledObj instanceof Boolean) {
               executionConfig.setParallelEnabled((Boolean)parallelEnabledObj);
            }

            Object maxParallelismObj = executionMap.get("max-parallelism");
            if (maxParallelismObj instanceof Number) {
               executionConfig.setMaxParallelism(((Number)maxParallelismObj).intValue());
            }

            LogUtils.info("\u89e3\u6790\u6267\u884c\u914d\u7f6e: defaultTimeoutMs={}, defaultRetryCount={}, defaultRetryDelayMs={}, parallelEnabled={}, maxParallelism={}", new Object[]{executionConfig.getDefaultTimeoutMs(), executionConfig.getDefaultRetryCount(), executionConfig.getDefaultRetryDelayMs(), executionConfig.isParallelEnabled(), executionConfig.getMaxParallelism()});
            return executionConfig;
         }
      }
   }
}
