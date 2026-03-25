package com.kuma.boot.encrypt.encrypt1.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "spring.encrypt"
)
public class EncryptProperties {
   private static final String DEFAULT_KEY = "www.itboyhub.com";
   private String key = "www.itboyhub.com";

   public String getKey() {
      return this.key;
   }

   public void setKey(String key) {
      this.key = key;
   }
}
