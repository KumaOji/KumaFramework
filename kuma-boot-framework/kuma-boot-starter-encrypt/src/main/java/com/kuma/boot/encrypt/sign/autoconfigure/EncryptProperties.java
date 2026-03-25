package com.kuma.boot.encrypt.sign.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(
   prefix = "kuma.boot.sign.encrypt"
)
public class EncryptProperties {
   public static final String PREFIX = "kuma.boot.sign.encrypt";
   private String resultName = "result";

   public String getResultName() {
      return this.resultName;
   }

   public void setResultName(String resultName) {
      this.resultName = resultName;
   }
}
