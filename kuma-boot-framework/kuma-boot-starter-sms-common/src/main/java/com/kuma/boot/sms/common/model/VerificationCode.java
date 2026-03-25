package com.kuma.boot.sms.common.model;

import java.time.LocalDateTime;

public class VerificationCode {
   private String phone;
   private String code;
   private String identificationCode;
   private LocalDateTime retryTime;
   private LocalDateTime expirationTime;

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

   public LocalDateTime getRetryTime() {
      return this.retryTime;
   }

   public void setRetryTime(LocalDateTime retryTime) {
      this.retryTime = retryTime;
   }

   public LocalDateTime getExpirationTime() {
      return this.expirationTime;
   }

   public void setExpirationTime(LocalDateTime expirationTime) {
      this.expirationTime = expirationTime;
   }
}
