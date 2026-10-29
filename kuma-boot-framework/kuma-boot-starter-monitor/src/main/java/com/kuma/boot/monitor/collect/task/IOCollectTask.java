package com.kuma.boot.monitor.collect.task;

import com.kuma.boot.core.utils.convert.ConvertUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.annotation.FieldReport;
import com.kuma.boot.monitor.autoconfigure.properties.CollectTaskProperties;
import com.kuma.boot.monitor.collect.AbstractCollectTask;
import com.kuma.boot.monitor.collect.CollectInfo;
import com.kuma.boot.monitor.enums.WarnTypeEnum;
import com.kuma.boot.monitor.utils.ProcessUtils;
import java.io.File;
import java.io.FileOutputStream;

public class IOCollectTask extends AbstractCollectTask {
   private static final String TASK_NAME = "kmc.monitor.collect.io";
   private final CollectTaskProperties properties;

   public IOCollectTask(CollectTaskProperties properties) {
      this.properties = properties;
   }

   public int getTimeSpan() {
      return this.properties.getIoTimeSpan();
   }

   public String getDesc() {
      return this.getClass().getName();
   }

   public String getName() {
      return "kmc.monitor.collect.io";
   }

   public boolean getEnabled() {
      return this.properties.isIoEnabled();
   }

   protected CollectInfo getData() {
      try {
         IoInfo ioInfo = new IoInfo();
         File file = new File(".");
         ioInfo.currentDirUsableSize = file.getUsableSpace() / this.byteToMb;
         ioInfo.currentDirTotalSize = file.getTotalSpace() / this.byteToMb;
         ioInfo.currentDir = file.getAbsolutePath();
         long processReadSize = (Long)ConvertUtils.convert(ProcessUtils.execCmd("cat /proc/$PID/io |egrep -E 'read_bytes'|awk '{print $2}'".replaceAll("\\$PID", ProcessUtils.getProcessID())), Long.class);
         ioInfo.processReadSize = processReadSize > 0L ? processReadSize / this.byteToMb : processReadSize;
         long processWriteSize = (Long)ConvertUtils.convert(ProcessUtils.execCmd("cat /proc/$PID/io |egrep -E '^write_bytes'|awk '{print $2}'".replaceAll("\\$PID", ProcessUtils.getProcessID())), Long.class);
         ioInfo.processWriteSize = processWriteSize > 0L ? processWriteSize / this.byteToMb : processWriteSize;
         ioInfo.processWa = (Double)ConvertUtils.convert(ProcessUtils.execCmd("top -bn1 | sed -n '3p'|cut -d, -f5 |awk '{print $1}'"), Double.class);
         return ioInfo;
      } catch (Exception e) {
         if (LogUtils.isErrorEnabled()) {
            LogUtils.error(e);
         }

         return null;
      }
   }

   public static boolean getIsAutoClear() {
      return true;
   }

   public static void clearLog() {
      if (getIsAutoClear()) {
         StringBuilder info = new StringBuilder();
         String[] logs = new String[]{"catlogs", "out.log", "app.log", "pinpoint-agent/log", "logs", "log"};

         for(String log : logs) {
            String result = clearFile(log);
            if (!result.isEmpty()) {
               info.append(log).append("[").append(result).append("];");
            }
         }

         AbstractCollectTask.notifyMessage(WarnTypeEnum.INFO, "自动清理日志成功", info.toString());
      }

   }

   private static String clearFile(String filepath) {
      File f = new File(filepath);
      if (!f.exists()) {
         return "";
      } else {
         try {
            if (f.isFile()) {
               try {
                  FileOutputStream out = new FileOutputStream(f);

                  try {
                     out.write(new byte[1]);
                  } catch (Throwable var8) {
                     try {
                        out.close();
                     } catch (Throwable var7) {
                        var8.addSuppressed(var7);
                     }

                     throw var8;
                  }

                  out.close();
               } catch (Exception e) {
                  LogUtils.error(e);
               }
            } else if (f.isDirectory()) {
               File[] files = f.listFiles();

               for(File file : files) {
                  clearFile(file.getPath());
               }
            }

            f.delete();
            return "清理完毕";
         } catch (Exception e) {
            LogUtils.error(e);
            return "清理出错";
         }
      }
   }

   private static class IoInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.io.current.dir.usable.size",
         desc = "当前目录可用大小(M)"
      )
      private Long currentDirUsableSize = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.io.current.dir.total.size",
         desc = "当前目录总大小(M)"
      )
      private Long currentDirTotalSize = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.io.current.dir.path",
         desc = "当前目录路径"
      )
      private String currentDir = "";
      @FieldReport(
         name = "kmc.monitor.collect.io.process.read.size",
         desc = "当前进程的读io(B)"
      )
      private Long processReadSize = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.io.process.write.size",
         desc = "当前进程的写io(B)"
      )
      private Long processWriteSize = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.io.process.wa",
         desc = "磁盘wa百分比"
      )
      private Double processWa = (double)0.0F;

      private IoInfo() {
      }
   }
}
