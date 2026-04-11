package com.taotao.boot.client.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(
   prefix = "kuma.boot.third-client.forest"
)
public class ForestProperties {
   public static final String PREFIX = "kuma.boot.third-client.forest";
   private boolean enabled = false;

   public ForestProperties() {
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
