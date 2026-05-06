package com.kuma.boot.useragent.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kuma.boot.useragent")
public class UserAgentProperties {
   public static final String PREFIX = "kuma.boot.useragent";
   private Boolean enabled = false;

   public UserAgentProperties() {
   }

   public Boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
   }
}
