package com.kuma.boot.encrypt.sign.autoconfigure;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(
   prefix = "kuma.boot.sign.encrypt.body"
)
public class EncryptBodyProperties {
   public static final String PREFIX = "kuma.boot.sign.encrypt.body";
   private String aesKey;
   private String desKey;
   private Charset encoding;

   public EncryptBodyProperties() {
      this.encoding = StandardCharsets.UTF_8;
   }

   public String getAesKey() {
      return this.aesKey;
   }

   public void setAesKey(String aesKey) {
      this.aesKey = aesKey;
   }

   public String getDesKey() {
      return this.desKey;
   }

   public void setDesKey(String desKey) {
      this.desKey = desKey;
   }

   public Charset getEncoding() {
      return this.encoding;
   }

   public void setEncoding(Charset encoding) {
      this.encoding = encoding;
   }
}
