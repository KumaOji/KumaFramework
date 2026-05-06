package com.kuma.boot.client.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.third-client.retrofit"
)
public class RetrofitProperties {
   public static final String PREFIX = "kuma.boot.third-client.retrofit";
   private boolean enabled = false;

   public RetrofitProperties() {
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
