package com.kuma.boot.openapi.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties("kuma.boot.openapi")
public class OpenApiProperties {
   public static final String PREFIX = "kuma.boot.openapi";
   private Boolean enabled = false;
   private Boolean client = false;
   private Boolean server = false;

   public Boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
   }

   public Boolean getClient() {
      return this.client;
   }

   public void setClient(Boolean client) {
      this.client = client;
   }

   public Boolean getServer() {
      return this.server;
   }

   public void setServer(Boolean server) {
      this.server = server;
   }
}
