package com.kuma.boot.grpc.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.grpc"
)
public class GrpcProperties {
   public static final String PREFIX = "kuma.boot.grpc";
   private Boolean enabled = true;

   public Boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
   }
}
