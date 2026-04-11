package com.kuma.boot.eventbus.disruptor.tmp3.util;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;

public class WaitStrategys {
   public static WaitStrategy BLOCKING_WAIT = new BlockingWaitStrategy();
   public static WaitStrategy SLEEPING_WAIT = new SleepingWaitStrategy();
   public static WaitStrategy YIELDING_WAIT = new YieldingWaitStrategy();
   public static WaitStrategy BUSYSPIN_WAIT = new BusySpinWaitStrategy();

   public WaitStrategys() {
   }
}
