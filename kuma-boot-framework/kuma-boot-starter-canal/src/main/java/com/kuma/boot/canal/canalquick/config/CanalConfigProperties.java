package com.kuma.boot.canal.canalquick.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.canal.quick"
)
public class CanalConfigProperties {
   public static final String PREFIX = "kuma.boot.canal.quick";
   private boolean enabled = false;
   private String host = "127.0.0.1";
   private Integer port = 1111;
   private String destination = "example";
   private String userName = "canal";
   private String password = "canal";
   private Integer batchSize = 1000;
   private String filter;
   private Boolean custom = true;

   public CanalConfigProperties() {
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public String getHost() {
      return this.host;
   }

   public void setHost(String host) {
      this.host = host;
   }

   public Integer getPort() {
      return this.port;
   }

   public void setPort(Integer port) {
      this.port = port;
   }

   public String getDestination() {
      return this.destination;
   }

   public void setDestination(String destination) {
      this.destination = destination;
   }

   public String getUserName() {
      return this.userName;
   }

   public void setUserName(String userName) {
      this.userName = userName;
   }

   public String getPassword() {
      return this.password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public Integer getBatchSize() {
      return this.batchSize;
   }

   public void setBatchSize(Integer batchSize) {
      this.batchSize = batchSize;
   }

   public String getFilter() {
      return this.filter;
   }

   public void setFilter(String filter) {
      this.filter = filter;
   }

   public Boolean getCustom() {
      return this.custom;
   }

   public void setCustom(Boolean custom) {
      this.custom = custom;
   }
}
