package com.kuma.boot.data.p6spy.logger;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.BatchFileLogger;
import com.kuma.boot.core.utils.context.ContextUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.springframework.context.ConfigurableApplicationContext;

public class FileLogger extends BatchFileLogger {
   private String fileName = null;
   private volatile PrintStream printStream = null;

   public FileLogger() {
   }

   private void init() {
      if (this.fileName == null) {
         throw new IllegalStateException("setLogfile() must be called before init()");
      } else {
         ConfigurableApplicationContext applicationContext = ContextUtils.getApplicationContext();
         String realFileName = applicationContext.getEnvironment().resolvePlaceholders(this.fileName);

         try {
            File file = new File(realFileName);
            if (!file.getParentFile().exists()) {
               file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
               file.createNewFile();
            }

            this.printStream = new PrintStream(new FileOutputStream(file, P6SpyOptions.getActiveInstance().getAppend()));
         } catch (IOException e) {
            throw new IllegalStateException("couldn't create PrintStream for " + this.fileName, e);
         }
      }
   }

   protected PrintStream getStream() {
      if (this.printStream == null) {
         synchronized(this) {
            if (this.printStream == null) {
               this.init();
            }
         }
      }

      return this.printStream;
   }

   public void setLogfile(String fileName) {
      this.fileName = fileName;
   }

   public void logSQL(int connectionId, String now, long elapsed, Category category, String prepared, String sql, String url) {
      super.logSQL(connectionId, now, elapsed, category, prepared, sql, url);
   }
}
