package com.kuma.boot.encrypt.crypto.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(
   prefix = "kuma.boot.crypto"
)
public class EncryptProperties {
   public static final String PREFIX = "kuma.boot.crypto";
   private Boolean enabled = true;

   public Boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
   }
}
