package com.kuma.boot.monitor.alarm.core.loader.helper;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.alarm.core.entity.AlarmConfig;
import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.io.monitor.FileEntry;

public class PropertiesConfListenerHelper {
   public PropertiesConfListenerHelper() {
   }

   public static boolean registerConfChangeListener(File file, Function<File, Map<String, AlarmConfig>> func) {
      try {
         long interval = TimeUnit.SECONDS.toMillis(5L);
         File dir = file.getParentFile();
         FileAlterationObserver observer = FileAlterationObserver.builder().setRootEntry(new FileEntry(dir)).setFileFilter(FileFilterUtils.and(new IOFileFilter[]{FileFilterUtils.fileFileFilter(), FileFilterUtils.nameFileFilter(file.getName())})).setIOCase((IOCase)null).get();
         observer.addListener(new MyFileListener(func));
         FileAlterationMonitor monitor = new FileAlterationMonitor(interval, new FileAlterationObserver[]{observer});
         monitor.start();
         return true;
      } catch (Exception e) {
         LogUtils.error("register properties change listener error! e:{}", new Object[]{e});
         return false;
      }
   }

   static final class MyFileListener extends FileAlterationListenerAdaptor {
      private final Function<File, Map<String, AlarmConfig>> func;

      public MyFileListener(Function<File, Map<String, AlarmConfig>> func) {
         this.func = func;
      }

      public void onFileChange(File file) {
         LogUtils.info("change >>> " + System.currentTimeMillis(), new Object[0]);
         Map<String, AlarmConfig> ans = (Map)this.func.apply(file);
         LogUtils.warn("PropertiesConfig changed! reload ans: {}", new Object[]{ans});
      }
   }
}
