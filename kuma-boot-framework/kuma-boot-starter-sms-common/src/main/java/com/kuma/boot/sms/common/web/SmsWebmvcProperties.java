package com.kuma.boot.sms.common.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.sms.web"
)
public class SmsWebmvcProperties {
   public static final String PREFIX = "kuma.boot.sms.web";
   public static final String DEFAULT_BASE_PATH = "/sms";
   private boolean enable = false;
   private String basePath = "/sms";
   private boolean enableSend = true;
   private boolean enableGet = true;
   private boolean enableVerify = true;
   private boolean enableNotice = true;

   public boolean isEnable() {
      return this.enable;
   }

   public void setEnable(boolean enable) {
      this.enable = enable;
   }

   public String getBasePath() {
      return this.basePath;
   }

   public void setBasePath(String basePath) {
      this.basePath = basePath;
   }

   public boolean isEnableSend() {
      return this.enableSend;
   }

   public void setEnableSend(boolean enableSend) {
      this.enableSend = enableSend;
   }

   public boolean isEnableGet() {
      return this.enableGet;
   }

   public void setEnableGet(boolean enableGet) {
      this.enableGet = enableGet;
   }

   public boolean isEnableVerify() {
      return this.enableVerify;
   }

   public void setEnableVerify(boolean enableVerify) {
      this.enableVerify = enableVerify;
   }

   public boolean isEnableNotice() {
      return this.enableNotice;
   }

   public void setEnableNotice(boolean enableNotice) {
      this.enableNotice = enableNotice;
   }
}
