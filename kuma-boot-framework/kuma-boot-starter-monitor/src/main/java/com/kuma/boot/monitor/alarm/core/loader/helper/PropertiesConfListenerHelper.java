package com.kuma.boot.monitor.alarm.core.loader.helper;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.alarm.core.entity.AlarmConfig;
import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
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
         FileAlterationObserver observer = FileAlterationObserver.builder()
               .setRootEntry(new FileEntry(dir))
               .setFileFilter(FileFilterUtils.and(
                     FileFilterUtils.fileFileFilter(),
                     FileFilterUtils.nameFileFilter(file.getName())))
               .setIOCase(null)
               .get();
         observer.addListener(new MyFileListener(func));
         FileAlterationMonitor monitor = new FileAlterationMonitor(interval, observer);
         monitor.start();
         return true;
      } catch (Exception e) {
         LogUtils.error(e, "register properties change listener error!");
         return false;
      }
   }

   static final class MyFileListener extends FileAlterationListenerAdaptor {
      private final Function<File, Map<String, AlarmConfig>> func;

      public MyFileListener(Function<File, Map<String, AlarmConfig>> func) {
         this.func = func;
      }

      public void onFileChange(File file) {
         LogUtils.info("change >>> {}", System.currentTimeMillis());
         Map<String, AlarmConfig> ans = this.func.apply(file);
         LogUtils.warn("PropertiesConfig changed! reload ans: {}", ans);
      }
   }
}
