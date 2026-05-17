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
         desc = "JVM \u5185\u5b58\u7edf\u8ba1"
      )
      private JvmTotalInfo totalInfo;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.eden.info",
         desc = "JVM Eden \u5e74\u8f7b\u4ee3\u5185\u5b58(M)"
      )
      private JVMEdenInfo edenInfo;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.survivor.info",
         desc = "JVM Survivor \u5e74\u8f7b\u4ee3\u5185\u5b58(M)"
      )
      private JVMSurvivorInfo survivorInfo;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.old.info",
         desc = "JVM Old \u8001\u5e74\u4ee3\u5185\u5b58(M)"
      )
      private JVMOldInfo genOldInfo;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.perm.info",
         desc = "JVM Perm \u6c38\u4e45\u4ee3\u5185\u5b58(M)"
      )
      private JVMPermInfo genPermInfo;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.codeCache.info",
         desc = "JVM CodeCache \u7f16\u8bd1\u7801\u7f13\u5b58\u5185\u5b58(M)"
      )
      private JVMCodeCacheInfo genCodeCache;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.metaspace.info",
         desc = "JVM metaspace \u5143\u6570\u636e\u7f13\u5b58\u5185\u5b58(M)"
      )
      private JVMMetaspaceInfo genMetaspace;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.compressed.class.space.info",
         desc = "JVM CompressedClassSpace \u7f13\u5b58\u5185\u5b58(M)"
      )
      private JVMCompressedClassSpaceInfo genCompressedClassSpace;

      private JvmInfo() {
      }
   }

   private static class JvmTotalInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.use",
         desc = "JVM\u5185\u5b58\u5df2\u7528\u7a7a\u95f4(M)"
      )
      private Long use = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.free",
         desc = "JVM\u5185\u5b58\u53ef\u7528\u7a7a\u95f4(M)"
      )
      private Long free = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.max",
         desc = "JVM\u5185\u5b58\u6700\u5927\u53ef\u7528\u7a7a\u95f4(M)"
      )
      private Long max = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.total",
         desc = "JVM\u5185\u5b58\u603b\u7a7a\u95f4(M)"
      )
      private Long total = 0L;

      private JvmTotalInfo() {
      }
   }

   private static class SystemInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.memory.os.use",
         desc = "Os\u5185\u5b58\u5df2\u7528\u7a7a\u95f4(M)"
      )
      private Long use = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.os.free",
         desc = "Os\u5185\u5b58\u53ef\u7528\u7a7a\u95f4(M)"
      )
      private Long free = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.os.total",
         desc = "Os\u5185\u5b58\u603b\u7a7a\u95f4(M)"
      )
      private Long total = 0L;

      private SystemInfo() {
      }
   }

   private static class MemeryInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm",
         desc = "JVM\u5185\u5b58\u7a7a\u95f4(M)"
      )
      private JvmInfo jvmInfo = new JvmInfo();
      @FieldReport(
         name = "kmc.monitor.collect.memory.system",
         desc = "Os\u5185\u5b58\u7a7a\u95f4(M)"
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
         desc = "perm \u521d\u59cb\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long init = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.perm.max",
         desc = "perm \u6700\u5927\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long max = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.perm.used",
         desc = "perm \u5df2\u4f7f\u7528\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long used = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.perm.committed",
         desc = "perm \u5df2\u7533\u8bf7\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long committed = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.perm.usedRate",
         desc = "perm \u4f7f\u7528\u7387 %"
      )
      private Long usedRate = 0L;

      private JVMPermInfo() {
      }
   }

   private static class JVMOldInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.old.init",
         desc = "old \u521d\u59cb\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long init = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.old.max",
         desc = "old \u6700\u5927\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long max = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.old.used",
         desc = "old \u5df2\u4f7f\u7528\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long used = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.old.committed",
         desc = "old \u5df2\u7533\u8bf7\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long committed = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.old.usedRate",
         desc = "old \u4f7f\u7528\u7387 %"
      )
      private Long usedRate = 0L;

      private JVMOldInfo() {
      }
   }

   private static class JVMSurvivorInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.survivor.init",
         desc = "survivor \u521d\u59cb\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long init = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.survivor.max",
         desc = "survivor \u6700\u5927\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long max = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.survivor.used",
         desc = "survivor \u5df2\u4f7f\u7528\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long used = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.survivor.committed",
         desc = "survivor \u5df2\u7533\u8bf7\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long committed = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.survivor.usedRate",
         desc = "survivor \u4f7f\u7528\u7387 %"
      )
      private Long usedRate = 0L;

      private JVMSurvivorInfo() {
      }
   }

   private static class JVMEdenInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.eden.init",
         desc = "eden \u521d\u59cb\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long init = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.eden.max",
         desc = "eden \u6700\u5927\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long max = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.eden.used",
         desc = "eden \u5df2\u4f7f\u7528\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long used = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.eden.committed",
         desc = "eden \u5df2\u7533\u8bf7\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long committed = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.eden.usedRate",
         desc = "eden \u4f7f\u7528\u7387 %"
      )
      private Long usedRate = 0L;

      private JVMEdenInfo() {
      }
   }

   private static class JVMCodeCacheInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.codeCache.init",
         desc = "codeCache \u521d\u59cb\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long init = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.codeCache.max",
         desc = "codeCache \u6700\u5927\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long max = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.codeCache.used",
         desc = "codeCache \u5df2\u4f7f\u7528\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long used = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.codeCache.committed",
         desc = "codeCache \u5df2\u7533\u8bf7\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long committed = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.codeCache.usedRate",
         desc = "codeCache \u4f7f\u7528\u7387 %"
      )
      private Long usedRate = 0L;

      private JVMCodeCacheInfo() {
      }
   }

   private static class JVMMetaspaceInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.metaspace.init",
         desc = "metaspace \u521d\u59cb\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long init = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.metaspace.max",
         desc = "metaspace \u6700\u5927\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long max = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.metaspace.used",
         desc = "metaspace \u5df2\u4f7f\u7528\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long used = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.metaspace.committed",
         desc = "metaspace \u5df2\u7533\u8bf7\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long committed = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.metaspace.usedRate",
         desc = "metaspace \u4f7f\u7528\u7387 %"
      )
      private Long usedRate = 0L;

      private JVMMetaspaceInfo() {
      }
   }

   private static class JVMCompressedClassSpaceInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.compressed.class.space.init",
         desc = "Compressed Class Space \u521d\u59cb\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long init = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.compressed.class.space.max",
         desc = "Compressed Class Space \u6700\u5927\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long max = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.compressed.class.space.used",
         desc = "Compressed Class Space \u5df2\u4f7f\u7528\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long used = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.compressed.class.space.committed",
         desc = "Compressed Class Space \u5df2\u7533\u8bf7\u5185\u5b58\u5927\u5c0f(M)"
      )
      private Long committed = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.memory.jvm.gen.compressed.class.space.usedRate",
         desc = "Compressed Class Space \u4f7f\u7528\u7387 %"
      )
      private Long usedRate = 0L;

      private JVMCompressedClassSpaceInfo() {
      }
   }
}
