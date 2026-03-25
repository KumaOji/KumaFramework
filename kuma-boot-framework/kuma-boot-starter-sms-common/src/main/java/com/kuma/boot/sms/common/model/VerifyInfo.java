package com.kuma.boot.sms.common.model;

public class VerifyInfo {
   private String phone;
   private String code;
   private String identificationCode;

   public String getPhone() {
      return this.phone;
   }

   public void setPhone(String phone) {
      this.phone = phone;
   }

   public String getCode() {
      return this.code;
   }

   public void setCode(String code) {
      this.code = code;
   }

   public String getIdentificationCode() {
      return this.identificationCode;
   }

   public void setIdentificationCode(String identificationCode) {
      this.identificationCode = identificationCode;
   }
}
