package com.kuma.boot.logger.eden.bootstrap.config;

public class BootstrapLogConfig {
   private boolean enabledMdc = true;

   public BootstrapLogConfig() {
   }

   public boolean isEnabledMdc() {
      return this.enabledMdc;
   }

   public void setEnabledMdc(boolean enabledMdc) {
      this.enabledMdc = enabledMdc;
   }
}
