package com.kuma.boot.elk.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.elk"
)
public class ElkProperties {
   public static final String PREFIX = "kuma.boot.elk";
   private boolean enabled = false;
   private String appName = "";
   private String springAppName = "";
   private String[] destinations = new String[]{"127.0.0.1:4560"};

   public ElkProperties() {
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public String getAppName() {
      return this.appName;
   }

   public void setAppName(String appName) {
      this.appName = appName;
   }

   public String getSpringAppName() {
      return this.springAppName;
   }

   public void setSpringAppName(String springAppName) {
      this.springAppName = springAppName;
   }

   public String[] getDestinations() {
      return this.destinations;
   }

   public void setDestinations(String[] destinations) {
      this.destinations = destinations;
   }
}
