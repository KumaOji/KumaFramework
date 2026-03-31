package com.kuma.boot.monitor.collect.task;

import com.kuma.boot.common.utils.exception.ExceptionUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.annotation.FieldReport;
import com.kuma.boot.monitor.autoconfigure.properties.CollectTaskProperties;
import com.kuma.boot.monitor.collect.AbstractCollectTask;
import com.kuma.boot.monitor.collect.CollectInfo;
import com.kuma.boot.monitor.enums.WarnTypeEnum;

public class UnCatchExceptionCollectTask extends AbstractCollectTask {
   private static final String TASK_NAME = "kmc.monitor.collect.unCatchException";
   private Throwable lastException = null;
   private final CollectTaskProperties properties;

   public UnCatchExceptionCollectTask(CollectTaskProperties properties) {
      this.properties = properties;
      Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
      if (!(handler instanceof DefaultUncaughtExceptionHandler)) {
         Thread.setDefaultUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(this, handler));
      }

   }

   public int getTimeSpan() {
      return -1;
   }

   public String getDesc() {
      return this.getClass().getName();
   }

   public String getName() {
      return "kmc.monitor.collect.unCatchException";
   }

   public boolean getEnabled() {
      return this.properties.isUncatchEnabled();
   }

   protected CollectInfo getData() {
      return new UnCatchInfo(StringUtils.nullToEmpty(ExceptionUtils.trace2String(this.lastException)));
   }

   public void close() throws Exception {
      Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
      if (handler instanceof DefaultUncaughtExceptionHandler) {
         Thread.setDefaultUncaughtExceptionHandler(((DefaultUncaughtExceptionHandler)handler).lastUncaughtExceptionHandler);
      }

   }

   public static class DefaultUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
      private final Thread.UncaughtExceptionHandler lastUncaughtExceptionHandler;
      private final UnCatchExceptionCollectTask unCatchExceptionCheckTask;

      public DefaultUncaughtExceptionHandler(UnCatchExceptionCollectTask unCatchExceptionCheckTask, Thread.UncaughtExceptionHandler lastUncaughtExceptionHandler) {
         this.unCatchExceptionCheckTask = unCatchExceptionCheckTask;
         this.lastUncaughtExceptionHandler = lastUncaughtExceptionHandler;
      }

      public void uncaughtException(Thread t, Throwable e) {
         try {
            if (e != null) {
               this.unCatchExceptionCheckTask.lastException = e;
               AbstractCollectTask.notifyMessage(WarnTypeEnum.ERROR, "\u672a\u6355\u83b7\u9519\u8bef", ExceptionUtils.trace2String(e));
               LogUtils.error(e, "\u672a\u6355\u83b7\u9519\u8bef", new Object[0]);
            }
         } catch (Exception var4) {
            if (LogUtils.isErrorEnabled()) {
               LogUtils.error(e);
            }
         }

         if (this.lastUncaughtExceptionHandler != null) {
            this.lastUncaughtExceptionHandler.uncaughtException(t, e);
         }

      }
   }

   private static class UnCatchInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.unCatchException.trace",
         desc = "\u672a\u6355\u83b7\u9519\u8bef\u5806\u6808"
      )
      private String trace = "";

      public UnCatchInfo(String trace) {
         this.trace = trace;
      }
   }
}
