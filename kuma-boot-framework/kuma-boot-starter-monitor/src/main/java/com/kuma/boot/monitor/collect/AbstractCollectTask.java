package com.kuma.boot.monitor.collect;

import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.enums.WarnTypeEnum;
import com.kuma.boot.monitor.model.Message;
import com.kuma.boot.monitor.model.Report;
import com.kuma.boot.monitor.warn.WarnProvider;

public abstract class AbstractCollectTask implements AutoCloseable {
   protected long byteToMb = 1048576L;
   protected volatile boolean start = true;
   private CollectInfo lastCollectInfo = null;
   protected long lastRunTime = System.currentTimeMillis();

   public AbstractCollectTask() {
   }

   public abstract int getTimeSpan();

   public abstract boolean getEnabled();

   public abstract String getDesc();

   public abstract String getName();

   public Report getReport() {
      if (this.start) {
         long time = System.currentTimeMillis() - this.lastRunTime;
         if (this.getTimeSpan() > 0 && time > (long)this.getTimeSpan() * 1000L) {
            this.lastRunTime = System.currentTimeMillis();
            this.lastCollectInfo = this.getData();
         }

         return this.lastCollectInfo == null ? null : new Report(this.lastCollectInfo);
      } else {
         return null;
      }
   }

   public static void notifyMessage(WarnTypeEnum type, String subject, String content) {
      LogUtils.warn("[warn]" + subject + "\r\n" + content, new Object[0]);
      WarnProvider warnProvider = (WarnProvider)ContextUtils.getBean(WarnProvider.class, false);
      if (warnProvider != null) {
         Message message = new Message();
         message.setWarnType(type);
         message.setTitle(subject);
         message.setContent(content);
         if (type == WarnTypeEnum.ERROR) {
            warnProvider.notifyNow(message);
         } else {
            warnProvider.notify(message);
         }
      }

   }

   public static void notifyMessage(Message message) {
      WarnProvider warnProvider = (WarnProvider)ContextUtils.getBean(WarnProvider.class, false);
      if (warnProvider != null) {
         if (message.getWarnType() == WarnTypeEnum.ERROR) {
            warnProvider.notifyNow(message);
         } else {
            warnProvider.notify(message);
         }
      }

   }

   protected CollectInfo getData() {
      return null;
   }

   public void close() throws Exception {
      this.start = false;
   }

   public boolean isStart() {
      return this.start;
   }

   public void setStart(boolean start) {
      this.start = start;
   }
}
