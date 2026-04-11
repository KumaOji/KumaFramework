package com.kuma.boot.mail.spring.model;

import java.io.File;

public class MailDetails {
   private String from;
   private String[] to;
   private String subject;
   private Boolean showHtml;
   private String content;
   private String[] cc;
   private String[] bcc;
   private File[] files;

   public MailDetails() {
   }

   public String getFrom() {
      return this.from;
   }

   public void setFrom(String from) {
      this.from = from;
   }

   public String[] getTo() {
      return this.to;
   }

   public void setTo(String[] to) {
      this.to = to;
   }

   public String getSubject() {
      return this.subject;
   }

   public void setSubject(String subject) {
      this.subject = subject;
   }

   public Boolean getShowHtml() {
      return this.showHtml;
   }

   public void setShowHtml(Boolean showHtml) {
      this.showHtml = showHtml;
   }

   public String getContent() {
      return this.content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public String[] getCc() {
      return this.cc;
   }

   public void setCc(String[] cc) {
      this.cc = cc;
   }

   public String[] getBcc() {
      return this.bcc;
   }

   public void setBcc(String[] bcc) {
      this.bcc = bcc;
   }

   public File[] getFiles() {
      return this.files;
   }

   public void setFiles(File[] files) {
      this.files = files;
   }
}
