package com.kuma.boot.monitor.collect.task;

import com.kuma.boot.common.utils.exception.ExceptionUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.annotation.FieldReport;
import com.kuma.boot.monitor.autoconfigure.properties.CollectTaskProperties;
import com.kuma.boot.monitor.collect.AbstractCollectTask;
import com.kuma.boot.monitor.collect.CollectInfo;
import java.lang.Thread.State;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;

public class ThreadCollectTask extends AbstractCollectTask {
   private static final String TASK_NAME = "kmc.monitor.collect.thread";
   private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
   private final CollectTaskProperties properties;
   private HashMap<Long, Long> lastThreadUserTime = new HashMap();

   public ThreadCollectTask(CollectTaskProperties properties) {
      this.properties = properties;
   }

   public int getTimeSpan() {
      return this.properties.getMonitorThreadTimeSpan();
   }

   public String getDesc() {
      return this.getClass().getName();
   }

   public String getName() {
      return "kmc.monitor.collect.thread";
   }

   public boolean getEnabled() {
      return this.properties.isMonitorThreadEnabled();
   }

   protected CollectInfo getData() {
      try {
         ThreadInfo threadInfo = new ThreadInfo();
         long[] deadlockedThreads = this.threadMXBean.findDeadlockedThreads();
         threadInfo.deadlockedThreadCount = deadlockedThreads == null ? 0 : deadlockedThreads.length;
         threadInfo.threadCount = this.threadMXBean.getThreadCount();
         java.lang.management.ThreadInfo runable = null;
         java.lang.management.ThreadInfo wait = null;
         java.lang.management.ThreadInfo block = null;
         HashMap<Long, Long> treadUserTime = new HashMap();

         for(java.lang.management.ThreadInfo info : this.threadMXBean.dumpAllThreads(false, false)) {
            treadUserTime.put(info.getThreadId(), this.threadMXBean.getThreadUserTime(info.getThreadId()));
            if (info.getThreadState() == State.RUNNABLE) {
               ++threadInfo.runnableThreadCount;
               if (runable == null) {
                  runable = info;
               } else {
                  Long lastvalue = (Long)this.lastThreadUserTime.get(info.getThreadId());
                  lastvalue = lastvalue == null ? 0L : lastvalue;
                  Long runablevalue = (Long)this.lastThreadUserTime.get(runable.getThreadId());
                  runablevalue = runablevalue == null ? 0L : runablevalue;
                  if (this.threadMXBean.getThreadUserTime(runable.getThreadId()) - runablevalue < this.threadMXBean.getThreadUserTime(info.getThreadId()) - lastvalue) {
                     runable = info;
                  }
               }
            } else if (info.getThreadState() == State.BLOCKED) {
               ++threadInfo.blockedThreadCount;
               if (block == null) {
                  block = info;
               } else if (block.getBlockedTime() < info.getBlockedTime()) {
                  block = info;
               }
            } else if (info.getThreadState() == State.WAITING) {
               ++threadInfo.waitingThreadCount;
               if (wait == null) {
                  wait = info;
               } else if (wait.getWaitedTime() < info.getWaitedTime()) {
                  wait = info;
               }
            }
         }

         this.lastThreadUserTime = treadUserTime;
         if (runable != null) {
            threadInfo.maxRunnableDetail = ExceptionUtils.trace2String(runable.getStackTrace());
         }

         if (wait != null) {
            threadInfo.maxWaitingDetail = ExceptionUtils.trace2String(wait.getStackTrace());
         }

         if (block != null) {
            threadInfo.maxBlockedDetail = ExceptionUtils.trace2String(block.getStackTrace());
         }

         return threadInfo;
      } catch (Exception e) {
         if (LogUtils.isErrorEnabled()) {
            LogUtils.error(e);
         }

         return null;
      }
   }

   private static class ThreadInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.thread.deadlocked.count",
         desc = "\u6b7b\u9501\u7ebf\u7a0b\u6570"
      )
      private Integer deadlockedThreadCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.thread.total",
         desc = "\u7ebf\u7a0b\u603b\u6570"
      )
      private Integer threadCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.thread.runnable.count",
         desc = "\u8fd0\u884c\u7ebf\u7a0b\u603b\u6570"
      )
      private Integer runnableThreadCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.thread.blocked.count",
         desc = "\u963b\u585e\u7ebf\u7a0b\u603b\u6570"
      )
      private Integer blockedThreadCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.thread.waiting.count",
         desc = "\u7b49\u5f85\u7ebf\u7a0b\u603b\u6570"
      )
      private Integer waitingThreadCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.thread.runnable.max.detail",
         desc = "\u6700\u8fd1\u8fd0\u884c\u6700\u8017\u65f6\u7684\u7ebf\u7a0b\u8be6\u60c5"
      )
      private String maxRunnableDetail = "";
      @FieldReport(
         name = "kmc.monitor.collect.thread.blocked.max.detail",
         desc = "\u963b\u585e\u6700\u8017\u65f6\u7684\u7ebf\u7a0b\u8be6\u60c5"
      )
      private String maxBlockedDetail = "";
      @FieldReport(
         name = "kmc.monitor.collect.thread.waiting.max.detail",
         desc = "\u7b49\u5f85\u6700\u8017\u65f6\u7684\u7ebf\u7a0b\u8be6\u60c5"
      )
      private String maxWaitingDetail = "";

      private ThreadInfo() {
      }
   }
}
