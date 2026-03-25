package com.kuma.boot.sms.common.repository;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.sms.common.model.VerificationCode;
import com.kuma.boot.sms.common.properties.VerificationCodeMemoryRepositoryProperties;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.jspecify.annotations.Nullable;

public class VerificationCodeMemoryRepository implements VerificationCodeRepository {
   private final Map cache = new ConcurrentHashMap();
   private final ScheduledThreadPoolExecutor gcScheduledExecutor = new ScheduledThreadPoolExecutor(1, (r) -> {
      Thread thread = new Thread(r);
      thread.setName("VerificationCodeMemoryRepository-GC");
      return thread;
   });
   private final Runnable task = this::gcHandler;
   private VerificationCodeMemoryRepositoryProperties config;

   public VerificationCodeMemoryRepository(VerificationCodeMemoryRepositoryProperties config) {
      this.setConfig(config);
   }

   public VerificationCode findOne(String phone, @Nullable String identificationCode) {
      String key = this.key(phone, identificationCode);
      VerificationCode verificationCode = (VerificationCode)this.cache.get(key);
      if (verificationCode == null) {
         LogUtils.debug("verificationCode is null, key: {}", new Object[]{key});
         return null;
      } else {
         LocalDateTime expirationTime = verificationCode.getExpirationTime();
         if (expirationTime != null && expirationTime.isBefore(LocalDateTime.now())) {
            LogUtils.debug("verificationCode is not null, but timeout, key: {}", new Object[]{key});
            this.cache.remove(key);
            return null;
         } else {
            return verificationCode;
         }
      }
   }

   public void save(VerificationCode verificationCode) {
      String key = this.key(verificationCode.getPhone(), verificationCode.getIdentificationCode());
      this.cache.put(key, verificationCode);
   }

   public void delete(String phone, @Nullable String identificationCode) {
      this.cache.remove(this.key(phone, identificationCode));
   }

   public void setConfig(VerificationCodeMemoryRepositoryProperties config) {
      this.config = config;
      this.initGcThread();
   }

   private String key(String phone, @Nullable String identificationCode) {
      return StringUtils.isBlank(identificationCode) ? phone : phone + "_" + identificationCode;
   }

   private void initGcThread() {
      long gcFrequency = this.config.getGcFrequency();
      if (gcFrequency <= 0L) {
         gcFrequency = 300L;
      }

      this.gcScheduledExecutor.remove(this.task);
      this.gcScheduledExecutor.scheduleAtFixedRate(this.task, gcFrequency, gcFrequency, TimeUnit.SECONDS);
   }

   private void gcHandler() {
      LocalDateTime now = LocalDateTime.now();
      boolean debug = LogUtils.isDebugEnabled();
      Set<String> keys = this.cache.keySet();
      List<String> removeKeys = debug ? new ArrayList(keys.size()) : null;
      keys.forEach((key) -> {
         VerificationCode verificationCode = (VerificationCode)this.cache.get(key);
         if (verificationCode != null) {
            LocalDateTime expirationTime = verificationCode.getExpirationTime();
            if (expirationTime != null && expirationTime.isBefore(now)) {
               this.cache.remove(key);
               if (debug) {
                  removeKeys.add(key);
               }
            }
         }

      });
      if (debug) {
         LogUtils.debug("gc remove keys: {}", new Object[]{removeKeys.size()});
      }

   }
}
