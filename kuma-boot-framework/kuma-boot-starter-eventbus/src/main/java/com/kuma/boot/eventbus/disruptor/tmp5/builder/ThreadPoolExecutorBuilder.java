package com.kuma.boot.eventbus.disruptor.tmp5.builder;

import com.kuma.boot.eventbus.disruptor.tmp5.enums.BlockingQueueTypeEnum;
import com.kuma.boot.eventbus.disruptor.tmp5.enums.RejectedPolicyTypeEnum;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorBuilder {
   private int defaultCoreSize;
   private int maxQueueSize;
   private long keepAliveTime;
   private TimeUnit unit;
   private BlockingQueueTypeEnum blockingQueueTypeEnum;
   private Boolean blockingQueueIsFair;
   private int queueInitMaxSize;
   private ThreadFactory threadFactory;
   private RejectedPolicyTypeEnum rejectedPolicyTypeEnum;

   public ThreadPoolExecutorBuilder() {
   }

   public int getDefaultCoreSize() {
      return this.defaultCoreSize;
   }

   public void setDefaultCoreSize(int defaultCoreSize) {
      this.defaultCoreSize = defaultCoreSize;
   }

   public int getMaxQueueSize() {
      return this.maxQueueSize;
   }

   public void setMaxQueueSize(int maxQueueSize) {
      this.maxQueueSize = maxQueueSize;
   }

   public long getKeepAliveTime() {
      return this.keepAliveTime;
   }

   public void setKeepAliveTime(long keepAliveTime) {
      this.keepAliveTime = keepAliveTime;
   }

   public TimeUnit getUnit() {
      return this.unit;
   }

   public void setUnit(TimeUnit unit) {
      this.unit = unit;
   }

   public BlockingQueueTypeEnum getBlockingQueueTypeEnum() {
      return this.blockingQueueTypeEnum;
   }

   public void setBlockingQueueTypeEnum(BlockingQueueTypeEnum blockingQueueTypeEnum) {
      this.blockingQueueTypeEnum = blockingQueueTypeEnum;
   }

   public Boolean getBlockingQueueIsFair() {
      return this.blockingQueueIsFair;
   }

   public void setBlockingQueueIsFair(Boolean blockingQueueIsFair) {
      this.blockingQueueIsFair = blockingQueueIsFair;
   }

   public int getQueueInitMaxSize() {
      return this.queueInitMaxSize;
   }

   public void setQueueInitMaxSize(int queueInitMaxSize) {
      this.queueInitMaxSize = queueInitMaxSize;
   }

   public ThreadFactory getThreadFactory() {
      return this.threadFactory;
   }

   public void setThreadFactory(ThreadFactory threadFactory) {
      this.threadFactory = threadFactory;
   }

   public RejectedPolicyTypeEnum getRejectedPolicyTypeEnum() {
      return this.rejectedPolicyTypeEnum;
   }

   public void setRejectedPolicyTypeEnum(RejectedPolicyTypeEnum rejectedPolicyTypeEnum) {
      this.rejectedPolicyTypeEnum = rejectedPolicyTypeEnum;
   }
}
