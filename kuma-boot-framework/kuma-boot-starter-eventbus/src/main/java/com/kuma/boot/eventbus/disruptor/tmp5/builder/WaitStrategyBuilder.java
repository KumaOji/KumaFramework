package com.kuma.boot.eventbus.disruptor.tmp5.builder;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.TimeoutBlockingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.kuma.boot.eventbus.disruptor.tmp5.enums.WaitStrategyEnum;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class WaitStrategyBuilder {
   private Long timeoutMillis = 10L;
   private Long sleepTimeNs = 100L;
   private Integer retries = 100;
   private WaitStrategyEnum waitStrategyEnum;

   public WaitStrategyBuilder() {
   }

   public WaitStrategy createWaitStrategy() {
      if (Objects.nonNull(this.waitStrategyEnum) && WaitStrategyEnum.SLEEP.getType().equals(this.waitStrategyEnum.getType())) {
         return new SleepingWaitStrategy(this.retries, this.sleepTimeNs);
      } else if (Objects.nonNull(this.waitStrategyEnum) && WaitStrategyEnum.YIELD.getType().equals(this.waitStrategyEnum.getType())) {
         return new YieldingWaitStrategy();
      } else if (Objects.nonNull(this.waitStrategyEnum) && WaitStrategyEnum.BLOCK.getType().equals(this.waitStrategyEnum.getType())) {
         return new BlockingWaitStrategy();
      } else if (Objects.nonNull(this.waitStrategyEnum) && WaitStrategyEnum.BUSYSPIN.getType().equals(this.waitStrategyEnum.getType())) {
         return new BusySpinWaitStrategy();
      } else {
         return (WaitStrategy)(Objects.nonNull(this.waitStrategyEnum) && WaitStrategyEnum.TIMEOUT.getType().equals(this.waitStrategyEnum.getType()) ? new TimeoutBlockingWaitStrategy(this.timeoutMillis, TimeUnit.MILLISECONDS) : new BlockingWaitStrategy());
      }
   }

   public WaitStrategy createWaitStrategy0() {
      return builder().build().createWaitStrategy();
   }

   public Long getTimeoutMillis() {
      return this.timeoutMillis;
   }

   public void setTimeoutMillis(Long timeoutMillis) {
      this.timeoutMillis = timeoutMillis;
   }

   public Long getSleepTimeNs() {
      return this.sleepTimeNs;
   }

   public void setSleepTimeNs(Long sleepTimeNs) {
      this.sleepTimeNs = sleepTimeNs;
   }

   public Integer getRetries() {
      return this.retries;
   }

   public void setRetries(Integer retries) {
      this.retries = retries;
   }

   public WaitStrategyEnum getWaitStrategyEnum() {
      return this.waitStrategyEnum;
   }

   public void setWaitStrategyEnum(WaitStrategyEnum waitStrategyEnum) {
      this.waitStrategyEnum = waitStrategyEnum;
   }

   public static InnerrWaitStrategyBuilder builder() {
      return new InnerrWaitStrategyBuilder();
   }

   public static final class InnerrWaitStrategyBuilder {
      private Long timeoutMillis = 10L;
      private Long sleepTimeNs = 100L;
      private Integer retries = 100;
      private WaitStrategyEnum waitStrategyEnum;

      private InnerrWaitStrategyBuilder() {
      }

      public InnerrWaitStrategyBuilder timeoutMillis(Long timeoutMillis) {
         this.timeoutMillis = timeoutMillis;
         return this;
      }

      public InnerrWaitStrategyBuilder sleepTimeNs(Long sleepTimeNs) {
         this.sleepTimeNs = sleepTimeNs;
         return this;
      }

      public InnerrWaitStrategyBuilder retries(Integer retries) {
         this.retries = retries;
         return this;
      }

      public InnerrWaitStrategyBuilder waitStrategyEnum(WaitStrategyEnum waitStrategyEnum) {
         this.waitStrategyEnum = waitStrategyEnum;
         return this;
      }

      public WaitStrategyBuilder build() {
         WaitStrategyBuilder waitStrategyBuilder = new WaitStrategyBuilder();
         waitStrategyBuilder.setTimeoutMillis(this.timeoutMillis);
         waitStrategyBuilder.setSleepTimeNs(this.sleepTimeNs);
         waitStrategyBuilder.setRetries(this.retries);
         waitStrategyBuilder.setWaitStrategyEnum(this.waitStrategyEnum);
         return waitStrategyBuilder;
      }
   }
}
