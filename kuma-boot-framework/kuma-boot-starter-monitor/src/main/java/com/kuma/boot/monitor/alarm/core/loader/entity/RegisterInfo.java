package com.kuma.boot.monitor.alarm.core.loader.entity;

import java.io.Serializable;

public class RegisterInfo implements Serializable {
   private String alarmConfPath;
   private Integer maxAlarmType;
   private String defaultAlarmUsers;
   private String appName;
   private String emailHost;
   private Integer emailPort;
   private String emailUname;
   private String emailToken;
   private String emailFrom;
   private Boolean emailSsl;

   public RegisterInfo() {
   }

   public String getAlarmConfPath() {
      return this.alarmConfPath;
   }

   public void setAlarmConfPath(String alarmConfPath) {
      this.alarmConfPath = alarmConfPath;
   }

   public Integer getMaxAlarmType() {
      return this.maxAlarmType;
   }

   public void setMaxAlarmType(Integer maxAlarmType) {
      this.maxAlarmType = maxAlarmType;
   }

   public String getDefaultAlarmUsers() {
      return this.defaultAlarmUsers;
   }

   public void setDefaultAlarmUsers(String defaultAlarmUsers) {
      this.defaultAlarmUsers = defaultAlarmUsers;
   }

   public String getAppName() {
      return this.appName;
   }

   public void setAppName(String appName) {
      this.appName = appName;
   }

   public String getEmailHost() {
      return this.emailHost;
   }

   public void setEmailHost(String emailHost) {
      this.emailHost = emailHost;
   }

   public Integer getEmailPort() {
      return this.emailPort;
   }

   public void setEmailPort(Integer emailPort) {
      this.emailPort = emailPort;
   }

   public String getEmailUname() {
      return this.emailUname;
   }

   public void setEmailUname(String emailUname) {
      this.emailUname = emailUname;
   }

   public String getEmailToken() {
      return this.emailToken;
   }

   public void setEmailToken(String emailToken) {
      this.emailToken = emailToken;
   }

   public String getEmailFrom() {
      return this.emailFrom;
   }

   public void setEmailFrom(String emailFrom) {
      this.emailFrom = emailFrom;
   }

   public Boolean getEmailSsl() {
      return this.emailSsl;
   }

   public void setEmailSsl(Boolean emailSsl) {
      this.emailSsl = emailSsl;
   }
}
