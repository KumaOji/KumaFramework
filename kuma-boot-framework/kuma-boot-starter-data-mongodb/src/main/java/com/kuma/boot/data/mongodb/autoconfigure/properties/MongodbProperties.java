package com.kuma.boot.data.mongodb.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.data.mongodb"
)
public class MongodbProperties {
   public static final String PREFIX = "kuma.boot.data.mongodb";
   private boolean enabled = false;
   private Boolean print = false;
   private Boolean slowQuery = false;
   private Long slowTime = 1000L;

   public MongodbProperties() {
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public Boolean getPrint() {
      return this.print;
   }

   public void setPrint(Boolean print) {
      this.print = print;
   }

   public Boolean getSlowQuery() {
      return this.slowQuery;
   }

   public void setSlowQuery(Boolean slowQuery) {
      this.slowQuery = slowQuery;
   }

   public Long getSlowTime() {
      return this.slowTime;
   }

   public void setSlowTime(Long slowTime) {
      this.slowTime = slowTime;
   }
}
