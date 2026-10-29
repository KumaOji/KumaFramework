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
   private static final String TASK_DESC = "CPU检查报表";
   private final OperatingSystemMXBean systemBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
   private final CollectTaskProperties properties;

   public CpuCollectTask(CollectTaskProperties properties) {
      this.properties = properties;
   }

   public int getTimeSpan() {
      return this.properties.getCpuTimeSpan();
   }

   public String getDesc() {
      return "CPU检查报表";
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
         desc = "进程cpu负载"
      )
      private Double processCpuLoad = (double)0.0F;
      @FieldReport(
         name = "kmc.monitor.collect.cpu.system",
         desc = "系统cpu负载"
      )
      private Double systemCpuLoad = (double)0.0F;
      @FieldReport(
         name = "kmc.monitor.collect.cpu.committed.virtual.memory.size",
         desc = "已提交的虚拟内存大小"
      )
      private Long committedVirtualMemorySize = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.cpu.total.swap.space.size",
         desc = "总交换空间大小"
      )
      private Long totalSwapSpaceSize = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.cpu.free.swap.space.size",
         desc = "空闲交换空间大小"
      )
      private Long freeSwapSpaceSize = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.cpu.process.cpu.time",
         desc = "处理cpu时间"
      )
      private Long processCpuTime = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.cpu.free.physical.memory.size",
         desc = "空闲的物理内存空间"
      )
      private Long freePhysicalMemorySize = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.cpu.total.physical.memory.size",
         desc = "总的物理内存空间"
      )
      private Long totalPhysicalMemorySize = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.cpu.core.num",
         desc = "系统cpu核心数"
      )
      private Integer cpuCoreNumber = 0;

      private CpuInfo() {
      }
   }
}
