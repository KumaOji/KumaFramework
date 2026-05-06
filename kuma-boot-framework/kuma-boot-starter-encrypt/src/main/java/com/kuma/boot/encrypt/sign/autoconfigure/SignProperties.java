package com.kuma.boot.encrypt.sign.autoconfigure;

import java.util.Arrays;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.sign"
)
public class SignProperties {
   public static final String PREFIX = "kuma.boot.sign";
   private boolean enabled = false;
   private String key;
   private List ignore = Arrays.asList("token", "sign", "dataSecret");

   public String getKey() {
      return this.key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public List getIgnore() {
      return this.ignore;
   }

   public void setIgnore(List ignore) {
      this.ignore = ignore;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
