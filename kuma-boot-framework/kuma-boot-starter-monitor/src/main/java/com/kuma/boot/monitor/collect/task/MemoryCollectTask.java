package com.kuma.boot.monitor.collect.task;

import com.sun.management.OperatingSystemMXBean;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.utils.reflect.ReflectionUtils;
import com.kuma.boot.monitor.annotation.FieldReport;
import com.kuma.boot.monitor.autoconfigure.properties.CollectTaskProperties;
import com.kuma.boot.monitor.collect.AbstractCollectTask;
import com.kuma.boot.monitor.collect.CollectInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.List;

public class MemoryCollectTask extends AbstractCollectTask {
   private static final String TASK_NAME = "kmc.monitor.collect.memory";
   private final OperatingSystemMXBean systemMXBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
   private final CollectTaskProperties properties;

   public MemoryCollectTask(CollectTaskProperties properties) {
      this.properties = properties;
   }

   public int getTimeSpan() {
      return this.properties.getMemeryTimeSpan();
   }

   public String getDesc() {
      return this.getClass().getName();
   }

   public String getName() {
      return "kmc.monitor.collect.memory";
   }

   public boolean getEnabled() {
      return this.properties.isMemeryEnabled();
   }

   protected CollectInfo getData() {
      try {
         JvmInfo jvmInfo = new JvmInfo();
         Runtime rt = Runtime.getRuntime();
         jvmInfo.totalInfo = new JvmTotalInfo();
         JvmTotalInfo totalInfo = jvmInfo.totalInfo;
         totalInfo.total = rt.totalMemory() / this.byteToMb;
         totalInfo.free = rt.freeMemory() / this.byteToMb;
         totalInfo.max = rt.maxMemory() / this.byteToMb;
         totalInfo.use = totalInfo.total - totalInfo.free;
         List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
         if (pools != null && !pools.isEmpty()) {
            for(MemoryPoolMXBean pool : pools) {
               String name = pool.getName();
               Object jvmGen = null;
               if (name.contains("Eden")) {
                  jvmInfo.edenInfo = new JVMEdenInfo();
                  jvmGen = jvmInfo.edenInfo;
               } else if (name.contains("Code Cache")) {
                  jvmInfo.genCodeCache = new JVMCodeCacheInfo();
                  jvmGen = jvmInfo.genCodeCache;
               } else if (name.contains("Old")) {
                  jvmInfo.genOldInfo = new JVMOldInfo();
                  jvmGen = jvmInfo.genOldInfo;
               } else if (name.contains("Perm")) {
                  jvmInfo.genPermInfo = new JVMPermInfo();
                  jvmGen = jvmInfo.genPermInfo;
               } else if (name.contains("Survivor")) {
                  jvmInfo.survivorInfo = new JVMSurvivorInfo();
                  jvmGen = jvmInfo.survivorInfo;
               } else if (name.contains("Metaspace")) {
                  jvmInfo.genMetaspace = new JVMMetaspaceInfo();
                  jvmGen = jvmInfo.genMetaspace;
               } else if (name.contains("Compressed Class Space")) {
                  jvmInfo.genCompressedClassSpace = new JVMCompressedClassSpaceInfo();
                  jvmGen = jvmInfo.genCompressedClassSpace;
               }

               if (jvmGen != null && pool.getUsage() != null) {
                  ReflectionUtils.setFieldValue(ReflectionUtils.findField(jvmGen.getClass(), "init"), jvmGen, pool.getUsage().getInit() / this.byteToMb);
                  ReflectionUtils.setFieldValue(ReflectionUtils.findField(jvmGen.getClass(), "used"), jvmGen, pool.getUsage().getUsed() / this.byteToMb);
                  ReflectionUtils.setFieldValue(ReflectionUtils.findField(jvmGen.getClass(), "max"), jvmGen, pool.getUsage().getMax() / this.byteToMb);
                  long poolUsageCommitted = pool.getUsage().getCommitted();
                  ReflectionUtils.setFieldValue(ReflectionUtils.findField(jvmGen.getClass(), "committed"), jvmGen, poolUsageCommitted / this.byteToMb);
                  if (poolUsageCommitted > 0L) {
                     ReflectionUtils.setFieldValue(ReflectionUtils.findField(jvmGen.getClass(), "usedRate"), jvmGen, pool.getUsage().getUsed() * 100L / poolUsageCommitted);
                  }
               }
            }
         }

         SystemInfo systemInfo = new SystemInfo();
         systemInfo.free = this.systemMXBean.getFreeMemorySize() / this.byteToMb;
         systemInfo.total = this.systemMXBean.getFreeMemorySize() / this.byteToMb;
         systemInfo.use = systemInfo.total - systemInfo.free;
         return new MemeryInfo(jvmInfo, systemInfo);
      } catch (Exception e) {
         if (LogUtils.isErrorEnabled()) {
            LogUtils.error(e);
         }

         return null;
      }
   }

   private static class JvmInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.total.info",
         desc = "JVM 内存统计"
      )
      private JvmTotalInfo totalInfo;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.eden.info",
         desc = "JVM Eden 年轻代内存(M)"
      )
      private JVMEdenInfo edenInfo;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.survivor.info",
         desc = "JVM Survivor 年轻代内存(M)"
      )
      private JVMSurvivorInfo survivorInfo;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.old.info",
         desc = "JVM Old 老年代内存(M)"
      )
      private JVMOldInfo genOldInfo;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.perm.info",
         desc = "JVM Perm 永久代内存(M)"
      )
      private JVMPermInfo genPermInfo;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.codeCache.info",
         desc = "JVM CodeCache 编译码缓存内存(M)"
      )
      private JVMCodeCacheInfo genCodeCache;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.metaspace.info",
         desc = "JVM metaspace 元数据缓存内存(M)"
      )
      private JVMMetaspaceInfo genMetaspace;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.compressed.class.space.info",
         desc = "JVM CompressedClassSpace 缓存内存(M)"
      )
      private JVMCompressedClassSpaceInfo genCompressedClassSpace;

      private JvmInfo() {
      }
   }

   private static class JvmTotalInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.use",
         desc = "JVM内存已用空间(M)"
      )
      private Long use = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.free",
         desc = "JVM内存可用空间(M)"
      )
      private Long free = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.max",
         desc = "JVM内存最大可用空间(M)"
      )
      private Long max = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.total",
         desc = "JVM内存总空间(M)"
      )
      private Long total = 0L;

      private JvmTotalInfo() {
      }
   }

   private static class SystemInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.memory.os.use",
         desc = "Os内存已用空间(M)"
      )
      private Long use = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.os.free",
         desc = "Os内存可用空间(M)"
      )
      private Long free = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.os.total",
         desc = "Os内存总空间(M)"
      )
      private Long total = 0L;

      private SystemInfo() {
      }
   }

   private static class MemeryInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm",
         desc = "JVM内存空间(M)"
      )
      private JvmInfo jvmInfo = new JvmInfo();
      @FieldReport(
         name = "kmc.monitor.collect.memory.system",
         desc = "Os内存空间(M)"
      )
      private SystemInfo systemInfo = new SystemInfo();

      public MemeryInfo(JvmInfo jvmInfo, SystemInfo systemInfo) {
         this.jvmInfo = jvmInfo;
         this.systemInfo = systemInfo;
      }
   }

   private static class JVMPermInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.perm.init",
         desc = "perm 初始内存大小(M)"
      )
      private Long init = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.perm.max",
         desc = "perm 最大内存大小(M)"
      )
      private Long max = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.perm.used",
         desc = "perm 已使用内存大小(M)"
      )
      private Long used = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.perm.committed",
         desc = "perm 已申请内存大小(M)"
      )
      private Long committed = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.perm.usedRate",
         desc = "perm 使用率 %"
      )
      private Long usedRate = 0L;

      private JVMPermInfo() {
      }
   }

   private static class JVMOldInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.old.init",
         desc = "old 初始内存大小(M)"
      )
      private Long init = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.old.max",
         desc = "old 最大内存大小(M)"
      )
      private Long max = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.old.used",
         desc = "old 已使用内存大小(M)"
      )
      private Long used = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.old.committed",
         desc = "old 已申请内存大小(M)"
      )
      private Long committed = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.old.usedRate",
         desc = "old 使用率 %"
      )
      private Long usedRate = 0L;

      private JVMOldInfo() {
      }
   }

   private static class JVMSurvivorInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.survivor.init",
         desc = "survivor 初始内存大小(M)"
      )
      private Long init = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.survivor.max",
         desc = "survivor 最大内存大小(M)"
      )
      private Long max = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.survivor.used",
         desc = "survivor 已使用内存大小(M)"
      )
      private Long used = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.survivor.committed",
         desc = "survivor 已申请内存大小(M)"
      )
      private Long committed = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.survivor.usedRate",
         desc = "survivor 使用率 %"
      )
      private Long usedRate = 0L;

      private JVMSurvivorInfo() {
      }
   }

   private static class JVMEdenInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.eden.init",
         desc = "eden 初始内存大小(M)"
      )
      private Long init = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.eden.max",
         desc = "eden 最大内存大小(M)"
      )
      private Long max = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.eden.used",
         desc = "eden 已使用内存大小(M)"
      )
      private Long used = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.eden.committed",
         desc = "eden 已申请内存大小(M)"
      )
      private Long committed = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.eden.usedRate",
         desc = "eden 使用率 %"
      )
      private Long usedRate = 0L;

      private JVMEdenInfo() {
      }
   }

   private static class JVMCodeCacheInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.codeCache.init",
         desc = "codeCache 初始内存大小(M)"
      )
      private Long init = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.codeCache.max",
         desc = "codeCache 最大内存大小(M)"
      )
      private Long max = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.codeCache.used",
         desc = "codeCache 已使用内存大小(M)"
      )
      private Long used = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.codeCache.committed",
         desc = "codeCache 已申请内存大小(M)"
      )
      private Long committed = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.codeCache.usedRate",
         desc = "codeCache 使用率 %"
      )
      private Long usedRate = 0L;

      private JVMCodeCacheInfo() {
      }
   }

   private static class JVMMetaspaceInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.metaspace.init",
         desc = "metaspace 初始内存大小(M)"
      )
      private Long init = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.metaspace.max",
         desc = "metaspace 最大内存大小(M)"
      )
      private Long max = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.metaspace.used",
         desc = "metaspace 已使用内存大小(M)"
      )
      private Long used = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.metaspace.committed",
         desc = "metaspace 已申请内存大小(M)"
      )
      private Long committed = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.metaspace.usedRate",
         desc = "metaspace 使用率 %"
      )
      private Long usedRate = 0L;

      private JVMMetaspaceInfo() {
      }
   }

   private static class JVMCompressedClassSpaceInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.compressed.class.space.init",
         desc = "Compressed Class Space 初始内存大小(M)"
      )
      private Long init = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.compressed.class.space.max",
         desc = "Compressed Class Space 最大内存大小(M)"
      )
      private Long max = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.compressed.class.space.used",
         desc = "Compressed Class Space 已使用内存大小(M)"
      )
      private Long used = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.compressed.class.space.committed",
         desc = "Compressed Class Space 已申请内存大小(M)"
      )
      private Long committed = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.compressed.class.space.usedRate",
         desc = "Compressed Class Space 使用率 %"
      )
      private Long usedRate = 0L;

      private JVMCompressedClassSpaceInfo() {
      }
   }
}
