package com.kuma.boot.monitor.monitor.monitor;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.monitor.registry.Registry;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RegistryInstanceManager {
   @Value("${app.monitor.warn.time:2000}")
   private long warnTime = 2000L;
   @Resource
   private List<Registry> registryList;

   public RegistryInstanceManager() {
   }

   public void record(String key, long time) {
      this.record(key, (String)null, time);
   }

   public void record(String key, String tag, long time) {
      if (this.warnTime > 0L && time > this.warnTime) {
         LogUtils.warn("key:{},tag:{} is invoke too long,time:{}", new Object[]{key, tag, time});
      }

      this.registryList.forEach((registry) -> registry.record(key, tag, time));
   }

   public void count(String key) {
      this.count(key, (double)1.0F);
   }

   public void count(String key, String tag) {
      this.count(key, tag, (double)1.0F);
   }

   public void count(String key, double count) {
      this.count(key, (String)null, count);
   }

   public void count(String key, String tag, double count) {
      if (!(count <= (double)0.0F)) {
         if (tag == null) {
            tag = "";
         }

         for(Registry registry : this.registryList) {
            registry.count(key, tag, count);
         }

      }
   }
}
