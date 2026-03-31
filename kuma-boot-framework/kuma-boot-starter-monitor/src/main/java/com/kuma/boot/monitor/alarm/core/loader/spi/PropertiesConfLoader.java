package com.kuma.boot.monitor.alarm.core.loader.spi;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.alarm.core.entity.AlarmConfig;
import com.kuma.boot.monitor.alarm.core.loader.api.IConfLoader;
import com.kuma.boot.monitor.alarm.core.loader.entity.RegisterInfo;
import com.kuma.boot.monitor.alarm.core.loader.helper.PropertiesConfListenerHelper;
import com.kuma.boot.monitor.alarm.core.loader.helper.RegisterInfoLoaderHelper;
import com.kuma.boot.monitor.alarm.core.loader.parse.AlarmConfParse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;

public class PropertiesConfLoader implements IConfLoader {
   private RegisterInfo registerInfo;
   private Map<String, AlarmConfig> cacheMap;
   private AlarmConfig defaultAlarmConfig;

   public PropertiesConfLoader() {
   }

   public boolean load() {
      this.registerInfo = RegisterInfoLoaderHelper.load();
      if (this.registerInfo == null) {
         return false;
      } else {
         String path = this.registerInfo.getAlarmConfPath();
         File file;
         if (path.startsWith("/")) {
            file = new File(path);
         } else {
            URL url = this.getClass().getClassLoader().getResource(path);
            file = new File(url.getFile());
         }

         Map<String, AlarmConfig> tmp = this.init(file);
         boolean ans = tmp != null;
         ans = ans && PropertiesConfListenerHelper.registerConfChangeListener(file, this::init);
         if (ans) {
            LogUtils.info("PropertiesConfLoader registerConfChangeListener success!", new Object[0]);
         }

         return ans;
      }
   }

   private Map<String, AlarmConfig> init(File file) {
      Map<String, AlarmConfig> tmp;
      try {
         List<String> list = IOUtils.readLines(new FileInputStream(file), "utf-8");
         String config = Joiner.on("").join(list);
         tmp = AlarmConfParse.parseConfig(config, Splitter.on(",").splitToList(this.registerInfo.getDefaultAlarmUsers()));
      } catch (IOException e) {
         LogUtils.error("load config into cacheMap error! e: {}", new Object[]{e});
         return null;
      }

      if (tmp != null) {
         this.cacheMap = tmp;
         this.defaultAlarmConfig = (AlarmConfig)this.cacheMap.get("default");
      }

      return tmp;
   }

   public RegisterInfo getRegisterInfo() {
      return this.registerInfo;
   }

   public boolean alarmEnable(String alarmKey) {
      return true;
   }

   public boolean containAlarmConfig(String alarmKey) {
      return this.cacheMap.containsKey(alarmKey);
   }

   public AlarmConfig getAlarmConfigOrDefault(String alarmKey) {
      return (AlarmConfig)this.cacheMap.getOrDefault(alarmKey, this.defaultAlarmConfig);
   }
}
