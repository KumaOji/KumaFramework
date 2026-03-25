package com.kuma.boot.sms.common.properties;

import com.kuma.boot.sms.common.enums.RejectPolicy;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(
   prefix = "kuma.boot.sms.async"
)
public class SmsAsyncProperties {
   public static final String PREFIX = "kuma.boot.sms.async";
   private boolean enable = true;
   private int corePoolSize = Runtime.getRuntime().availableProcessors();
   private int maximumPoolSize = Runtime.getRuntime().availableProcessors() * 2;
   private long keepAliveTime = 60L;
   private TimeUnit unit;
   private int queueCapacity;
   private RejectPolicy rejectPolicy;

   public SmsAsyncProperties() {
      this.unit = TimeUnit.SECONDS;
      this.queueCapacity = Integer.MAX_VALUE;
      this.rejectPolicy = RejectPolicy.Abort;
   }

   public RejectPolicy getRejectPolicy() {
      return this.rejectPolicy;
   }

   public void setRejectPolicy(RejectPolicy rejectPolicy) {
      this.rejectPolicy = rejectPolicy;
   }

   public boolean isEnable() {
      return this.enable;
   }

   public void setEnable(boolean enable) {
      this.enable = enable;
   }

   public int getCorePoolSize() {
      return this.corePoolSize;
   }

   public void setCorePoolSize(int corePoolSize) {
      this.corePoolSize = corePoolSize;
   }

   public int getMaximumPoolSize() {
      return this.maximumPoolSize;
   }

   public void setMaximumPoolSize(int maximumPoolSize) {
      this.maximumPoolSize = maximumPoolSize;
   }

   public long getKeepAliveTime() {
      return this.keepAliveTime;
   }

   public void setKeepAliveTime(long keepAliveTime) {
      this.keepAliveTime = keepAliveTime;
   }

   public TimeUnit getUnit() {
      return this.unit;
   }

   public void setUnit(TimeUnit unit) {
      this.unit = unit;
   }

   public int getQueueCapacity() {
      return this.queueCapacity;
   }

   public void setQueueCapacity(int queueCapacity) {
      this.queueCapacity = queueCapacity;
   }
}
