package com.kuma.boot.monitor.collect.task;

import com.sun.management.OperatingSystemMXBean;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.annotation.FieldReport;
import com.kuma.boot.monitor.autoconfigure.properties.CollectTaskProperties;
import com.kuma.boot.monitor.collect.AbstractCollectTask;
import com.kuma.boot.monitor.collect.CollectInfo;
import java.lang.management.ManagementFactory;

public class CpuCollectTask extends AbstractCollectTask {
   private static final String TASK_NAME = "kmc.monitor.collect.cpu";
   private static final String TASK_DESC = "CPU\u68c0\u67e5\u62a5\u8868";
   private final OperatingSystemMXBean systemBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
   private final CollectTaskProperties properties;

   public CpuCollectTask(CollectTaskProperties properties) {
      this.properties = properties;
   }

   public int getTimeSpan() {
      return this.properties.getCpuTimeSpan();
   }

   public String getDesc() {
      return "CPU\u68c0\u67e5\u62a5\u8868";
   }

   public String getName() {
      return "kmc.monitor.collect.cpu";
   }

   public boolean getEnabled() {
      return this.properties.isCpuEnabled();
   }

   protected CollectInfo getData() {
      try {
         CpuInfo info = new CpuInfo();
         info.processCpuLoad = this.systemBean.getProcessCpuLoad();
         info.systemCpuLoad = this.systemBean.getCpuLoad();
         info.committedVirtualMemorySize = this.systemBean.getCommittedVirtualMemorySize();
         info.totalSwapSpaceSize = this.systemBean.getTotalSwapSpaceSize();
         info.freeSwapSpaceSize = this.systemBean.getFreeSwapSpaceSize();
         info.processCpuTime = this.systemBean.getProcessCpuTime();
         info.freePhysicalMemorySize = this.systemBean.getFreeMemorySize();
         info.totalPhysicalMemorySize = this.systemBean.getTotalMemorySize();
         info.cpuCoreNumber = Runtime.getRuntime().availableProcessors();
         return info;
      } catch (Exception e) {
         if (LogUtils.isErrorEnabled()) {
            LogUtils.error(e);
         }

         return null;
      }
   }

   private static class CpuInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.cpu.process",
         desc = "\u8fdb\u7a0bcpu\u8d1f\u8f7d"
      )
      private Double processCpuLoad = (double)0.0F;
      @FieldReport(
         name = "kmc.monitor.collect.cpu.system",
         desc = "\u7cfb\u7edfcpu\u8d1f\u8f7d"
      )
      private Double systemCpuLoad = (double)0.0F;
      @FieldReport(
         name = "kmc.monitor.collect.cpu.committed.virtual.memory.size",
         desc = "\u5df2\u63d0\u4ea4\u7684\u865a\u62df\u5185\u5b58\u5927\u5c0f"
      )
      private Long committedVirtualMemorySize = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.cpu.total.swap.space.size",
         desc = "\u603b\u4ea4\u6362\u7a7a\u95f4\u5927\u5c0f"
      )
      private Long totalSwapSpaceSize = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.cpu.free.swap.space.size",
         desc = "\u7a7a\u95f2\u4ea4\u6362\u7a7a\u95f4\u5927\u5c0f"
      )
      private Long freeSwapSpaceSize = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.cpu.process.cpu.time",
         desc = "\u5904\u7406cpu\u65f6\u95f4"
      )
      private Long processCpuTime = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.cpu.free.physical.memory.size",
         desc = "\u7a7a\u95f2\u7684\u7269\u7406\u5185\u5b58\u7a7a\u95f4"
      )
      private Long freePhysicalMemorySize = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.cpu.total.physical.memory.size",
         desc = "\u603b\u7684\u7269\u7406\u5185\u5b58\u7a7a\u95f4"
      )
      private Long totalPhysicalMemorySize = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.cpu.core.num",
         desc = "\u7cfb\u7edfcpu\u6838\u5fc3\u6570"
      )
      private Integer cpuCoreNumber = 0;

      private CpuInfo() {
      }
   }
}
