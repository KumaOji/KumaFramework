package com.kuma.boot.canal.autoconfigure.properties;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.canal"
)
public class CanalProperties {
   public static final String PREFIX = "kuma.boot.canal";
   private boolean enabled = false;
   private Map<String, Instance> instances = new LinkedHashMap();

   public CanalProperties() {
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public Map<String, Instance> getInstances() {
      return this.instances;
   }

   public void setInstances(Map<String, Instance> instances) {
      this.instances = instances;
   }

   public static class Instance {
      private boolean clusterEnabled;
      private Set<String> zookeeperAddress = new LinkedHashSet();
      private String host = "127.1.1.1";
      private int port = 11111;
      private String userName = "";
      private String password = "";
      private int batchSize = 1000;
      private String filter;
      private int retryCount = 5;
      private long acquireInterval = 1000L;

      public Instance() {
      }

      public boolean getClusterEnabled() {
         return this.clusterEnabled;
      }

      public void setClusterEnabled(boolean clusterEnabled) {
         this.clusterEnabled = clusterEnabled;
      }

      public Set<String> getZookeeperAddress() {
         return this.zookeeperAddress;
      }

      public void setZookeeperAddress(Set<String> zookeeperAddress) {
         this.zookeeperAddress = zookeeperAddress;
      }

      public String getHost() {
         return this.host;
      }

      public void setHost(String host) {
         this.host = host;
      }

      public int getPort() {
         return this.port;
      }

      public void setPort(int port) {
         this.port = port;
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

      public int getBatchSize() {
         return this.batchSize;
      }

      public void setBatchSize(int batchSize) {
         this.batchSize = batchSize;
      }

      public String getFilter() {
         return this.filter;
      }

      public void setFilter(String filter) {
         this.filter = filter;
      }

      public int getRetryCount() {
         return this.retryCount;
      }

      public void setRetryCount(int retryCount) {
         this.retryCount = retryCount;
      }

      public long getAcquireInterval() {
         return this.acquireInterval;
      }

      public void setAcquireInterval(long acquireInterval) {
         this.acquireInterval = acquireInterval;
      }
   }
}
