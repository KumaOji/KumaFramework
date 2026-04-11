package com.kuma.boot.mail.spring.model;

import java.time.LocalDateTime;

public class MailSendInfo {
   private final MailDetails mailDetails;
   private LocalDateTime sentDate;
   private Boolean success;
   private String errorMsg;

   public MailSendInfo(MailDetails mailDetails) {
      this.mailDetails = mailDetails;
   }

   public MailDetails getMailDetails() {
      return this.mailDetails;
   }

   public LocalDateTime getSentDate() {
      return this.sentDate;
   }

   public void setSentDate(LocalDateTime sentDate) {
      this.sentDate = sentDate;
   }

   public Boolean getSuccess() {
      return this.success;
   }

   public void setSuccess(Boolean success) {
      this.success = success;
   }

   public String getErrorMsg() {
      return this.errorMsg;
   }

   public void setErrorMsg(String errorMsg) {
      this.errorMsg = errorMsg;
   }
}
