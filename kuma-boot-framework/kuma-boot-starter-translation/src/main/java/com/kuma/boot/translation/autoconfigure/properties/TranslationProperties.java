package com.kuma.boot.translation.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties("kuma.boot.translation")
public class TranslationProperties {
   public static final String PREFIX = "kuma.boot.translation";
   private Boolean enabled = false;

   public TranslationProperties() {
   }

   public Boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
   }
}
