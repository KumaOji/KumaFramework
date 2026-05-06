package com.kuma.boot.data.shardingsphere.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.data.shardingsphere"
)
public class ShardingJdbcProperties {
   public static final String PREIX = "kuma.boot.data.shardingsphere";
   private boolean enabled = false;

   public ShardingJdbcProperties() {
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
