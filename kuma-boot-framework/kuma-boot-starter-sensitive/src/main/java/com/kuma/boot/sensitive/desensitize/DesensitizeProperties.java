package com.kuma.boot.sensitive.desensitize;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.sensitive"
)
public class DesensitizeProperties {
   public static final String PREFIX = "kuma.boot.sensitive";
   private Boolean enabled = true;
   private Integer sensitiveLevel = 0;

   public DesensitizeProperties() {
   }

   public Boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
   }

   public Integer getSensitiveLevel() {
      return this.sensitiveLevel == null ? 0 : this.sensitiveLevel;
   }

   public void setSensitiveLevel(Integer sensitiveLevel) {
      this.sensitiveLevel = sensitiveLevel;
   }
}
