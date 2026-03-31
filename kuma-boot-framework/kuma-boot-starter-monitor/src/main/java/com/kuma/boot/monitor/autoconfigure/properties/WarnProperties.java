package com.kuma.boot.monitor.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(
   prefix = "kuma.boot.monitor.warn"
)
public class WarnProperties {
   public static final String PREFIX = "kuma.boot.monitor.warn";
   private boolean enabled = true;
   private int cacheCount = 3;
   private int timeSpan = 30;
   private int duplicateTimeSpan = 2;
   private boolean dingDingWarnEnabled = true;
   private boolean smsWarnEnabled = true;
   private boolean emailWarnEnabled = true;
   private String dingdingFilterIP;
   private String flybookFilterIP;

   public WarnProperties() {
   }

   public int getCacheCount() {
      return this.cacheCount;
   }

   public void setCacheCount(int cacheCount) {
      this.cacheCount = cacheCount;
   }

   public int getTimeSpan() {
      return this.timeSpan;
   }

   public void setTimeSpan(int timeSpan) {
      this.timeSpan = timeSpan;
   }

   public int getDuplicateTimeSpan() {
      return this.duplicateTimeSpan;
   }

   public void setDuplicateTimeSpan(int duplicateTimeSpan) {
      this.duplicateTimeSpan = duplicateTimeSpan;
   }

   public String getDingdingFilterIP() {
      return this.dingdingFilterIP;
   }

   public void setDingdingFilterIP(String dingdingFilterIP) {
      this.dingdingFilterIP = dingdingFilterIP;
   }

   public String getFlybookFilterIP() {
      return this.flybookFilterIP;
   }

   public void setFlybookFilterIP(String flybookFilterIP) {
      this.flybookFilterIP = flybookFilterIP;
   }

   public boolean isDingDingWarnEnabled() {
      return this.dingDingWarnEnabled;
   }

   public void setDingDingWarnEnabled(boolean dingDingWarnEnabled) {
      this.dingDingWarnEnabled = dingDingWarnEnabled;
   }

   public boolean getSmsWarnEnabled() {
      return this.smsWarnEnabled;
   }

   public void setSmsWarnEnabled(boolean smsWarnEnabled) {
      this.smsWarnEnabled = smsWarnEnabled;
   }

   public boolean getEmailWarnEnabled() {
      return this.emailWarnEnabled;
   }

   public void setEmailWarnEnabled(boolean emailWarnEnabled) {
      this.emailWarnEnabled = emailWarnEnabled;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public boolean isSmsWarnEnabled() {
      return this.smsWarnEnabled;
   }

   public boolean isEmailWarnEnabled() {
      return this.emailWarnEnabled;
   }
}
