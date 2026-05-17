package com.kuma.boot.monitor.collect.task;

import com.kuma.boot.core.utils.convert.ConvertUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.annotation.FieldReport;
import com.kuma.boot.monitor.autoconfigure.properties.CollectTaskProperties;
import com.kuma.boot.monitor.collect.AbstractCollectTask;
import com.kuma.boot.monitor.collect.CollectInfo;
import com.kuma.boot.monitor.utils.ProcessUtils;

public class NetworkCollectTask extends AbstractCollectTask {
   private static final String TASK_NAME = "kmc.monitor.collect.network";
   private final CollectTaskProperties properties;

   public NetworkCollectTask(CollectTaskProperties properties) {
      this.properties = properties;
   }

   public int getTimeSpan() {
      return this.properties.getNetworkTimeSpan();
   }

   public String getDesc() {
      return this.getClass().getName();
   }

   public String getName() {
      return "kmc.monitor.collect.network";
   }

   public boolean getEnabled() {
      return this.properties.isNetworkEnabled();
   }

   protected CollectInfo getData() {
      try {
         NetworkInfo ioInfo = new NetworkInfo();
         ioInfo.processSysTcpListenNum = (Long)ConvertUtils.convert(ProcessUtils.execCmd("netstat -anp |awk '/^tcp/ {print $6,$7}' |cut -d/ -f1 |egrep -w 'LISTEN' |wc -l"), Long.class);
         ioInfo.processSysTcpEstablishedNum = (Long)ConvertUtils.convert(ProcessUtils.execCmd("netstat -anp |awk '/^tcp/ {print $6,$7}' |cut -d/ -f1 |egrep -w 'ESTABLISHED' |wc -l"), Long.class);
         ioInfo.processSysTcpTimeWaitNum = (Long)ConvertUtils.convert(ProcessUtils.execCmd("netstat -anp |awk '/^tcp/ {print $6,$7}' |cut -d/ -f1 |egrep -w 'TIME_WAIT' |wc -l"), Long.class);
         ioInfo.processTcpListenNum = (Long)ConvertUtils.convert(ProcessUtils.execCmd("netstat -anp |awk '/^tcp/ {print $6,$7}' |cut -d/ -f1  |egrep -w '$PID' |egrep -w 'LISTEN' |wc -l".replaceAll("\\$PID", ProcessUtils.getProcessID())), Long.class);
         ioInfo.processTcpEstablishedNum = (Long)ConvertUtils.convert(ProcessUtils.execCmd("netstat -anp |awk '/^tcp/ {print $6,$7}' |cut -d/ -f1  |egrep -w '$PID' |egrep -w 'ESTABLISHED' |wc -l".replaceAll("\\$PID", ProcessUtils.getProcessID())), Long.class);
         return ioInfo;
      } catch (Exception e) {
         if (LogUtils.isErrorEnabled()) {
            LogUtils.error(e);
         }

         return null;
      }
   }

   private static class NetworkInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.network.process.tcp.listen.number",
         desc = "\u5f53\u524d\u8fdb\u7a0bTCP LISTEN\u72b6\u6001\u8fde\u63a5\u6570"
      )
      private Long processTcpListenNum = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.network.process.tcp.established.number",
         desc = "\u5f53\u524d\u8fdb\u7a0bTCP ESTABLISHED\u72b6\u6001\u8fde\u63a5\u6570"
      )
      private Long processTcpEstablishedNum = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.network.process.tcp.time_wait.number",
         desc = "\u5f53\u524d\u8fdb\u7a0bTCP TIME_WAIT\u8fde\u63a5\u6570"
      )
      private Long processTcpTimeWaitNum = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.network.sys.tcp.listen.number",
         desc = "\u7cfb\u7edfTCP LISTEN\u72b6\u6001\u8fde\u63a5\u6570"
      )
      private Long processSysTcpListenNum = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.network.sys.tcp.established.number",
         desc = "\u7cfb\u7edfTCP ESTABLISHED\u72b6\u6001\u8fde\u63a5\u6570"
      )
      private Long processSysTcpEstablishedNum = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.network.sys.tcp.time_wait.number",
         desc = "\u7cfb\u7edfTCP TIME_WAIT\u8fde\u63a5\u6570"
      )
      private Long processSysTcpTimeWaitNum = 0L;

      private NetworkInfo() {
      }
   }
}
