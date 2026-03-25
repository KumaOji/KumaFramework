package com.kuma.boot.encrypt.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties("kuma.boot.encrypt")
public class EncryptProperties {
   public static final String PREFIX = "kuma.boot.encrypt";
   private boolean enabled = false;

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
