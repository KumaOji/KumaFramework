package com.kuma.boot.websocket.original.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kuma.boot.websocket.original")
public class WebSocketOriginalProperties {
   public static final String PREFIX = "kuma.boot.websocket.original";
   private Boolean enabled = true;

   public WebSocketOriginalProperties() {
   }

   public Boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
   }
}
