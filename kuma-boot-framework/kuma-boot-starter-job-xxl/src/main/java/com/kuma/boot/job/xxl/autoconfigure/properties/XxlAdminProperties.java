package com.kuma.boot.job.xxl.autoconfigure.properties;

public class XxlAdminProperties {
   private String addresses;
   private String password;
   private String username;

   public XxlAdminProperties() {
   }

   public String getAddresses() {
      return this.addresses;
   }

   public void setAddresses(String addresses) {
      this.addresses = addresses;
   }

   public String getPassword() {
      return this.password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getUsername() {
      return this.username;
   }

   public void setUsername(String username) {
      this.username = username;
   }
}
