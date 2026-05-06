package com.kuma.boot.flowengine.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.flowengine"
)
public class FlowEngineProperties {
   public static final String PREFIX = "kuma.boot.flowengine";
   private boolean enabled = false;
   private String flowConfLocation;
   private boolean retryable;

   public FlowEngineProperties() {
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public String getFlowConfLocation() {
      return this.flowConfLocation;
   }

   public void setFlowConfLocation(String flowConfLocation) {
      this.flowConfLocation = flowConfLocation;
   }

   public boolean getRetryable() {
      return this.retryable;
   }

   public void setRetryable(boolean retryable) {
      this.retryable = retryable;
   }
}
