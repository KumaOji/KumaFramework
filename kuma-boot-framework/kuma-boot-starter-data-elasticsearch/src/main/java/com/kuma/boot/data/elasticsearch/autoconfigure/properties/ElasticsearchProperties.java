package com.kuma.boot.data.elasticsearch.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.data.elasticsearch"
)
public class ElasticsearchProperties {
   public static final String PREFIX = "kuma.boot.data.elasticsearch";
   private boolean enabled = false;

   public ElasticsearchProperties() {
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
