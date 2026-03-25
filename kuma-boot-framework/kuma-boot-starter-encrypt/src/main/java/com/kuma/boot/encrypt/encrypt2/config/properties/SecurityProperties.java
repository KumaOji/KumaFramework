package com.kuma.boot.encrypt.encrypt2.config.properties;

import com.kuma.boot.encrypt.encrypt2.constants.SecurityMode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "fzy.security"
)
public class SecurityProperties {
   private SecurityMode mode;
   private boolean enable;
   private String type;
   private String charset;
   private String secret;
   private int maxDeep;
   private String privateKey;
   private String publicKey;
   private List classPackage;

   public SecurityProperties() {
      this.mode = SecurityMode.BASE64;
      this.enable = true;
      this.type = "AES";
      this.charset = "UTF-8";
      this.secret = "+6cuvzvyrFZpRG9pf3r7eQ==";
      this.maxDeep = 5;
      this.classPackage = new ArrayList();
   }

   public SecurityMode getMode() {
      return this.mode;
   }

   public void setMode(SecurityMode mode) {
      this.mode = mode;
   }

   public boolean isEnable() {
      return this.enable;
   }

   public void setEnable(boolean enable) {
      this.enable = enable;
   }

   public String getType() {
      return this.type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getCharset() {
      return this.charset;
   }

   public void setCharset(String charset) {
      this.charset = charset;
   }

   public String getSecret() {
      return this.secret;
   }

   public void setSecret(String secret) {
      this.secret = secret;
   }

   public int getMaxDeep() {
      return this.maxDeep;
   }

   public void setMaxDeep(int maxDeep) {
      this.maxDeep = maxDeep;
   }

   public String getPrivateKey() {
      return this.privateKey;
   }

   public void setPrivateKey(String privateKey) {
      this.privateKey = privateKey;
   }

   public String getPublicKey() {
      return this.publicKey;
   }

   public void setPublicKey(String publicKey) {
      this.publicKey = publicKey;
   }

   public List getClassPackage() {
      return this.classPackage;
   }

   public void setClassPackage(List classPackage) {
      this.classPackage = classPackage;
   }
}
