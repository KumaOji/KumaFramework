package com.kuma.boot.job.xxl.autoconfigure.properties;

public class XxlExecutorProperties {
   private String title;
   private String appname;
   private String address;
   private String ip;
   private Integer port = 0;
   private String accessToken;
   private String logPath = "${user.home}/logs/${spring.application.name}/applogs/xxl-job/jobhandler";
   private Integer logRetentionDays = 30;

   public XxlExecutorProperties() {
   }

   public String getTitle() {
      return this.title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getAppname() {
      return this.appname;
   }

   public void setAppname(String appname) {
      this.appname = appname;
   }

   public String getAddress() {
      return this.address;
   }

   public void setAddress(String address) {
      this.address = address;
   }

   public String getIp() {
      return this.ip;
   }

   public void setIp(String ip) {
      this.ip = ip;
   }

   public Integer getPort() {
      return this.port;
   }

   public void setPort(Integer port) {
      this.port = port;
   }

   public String getAccessToken() {
      return this.accessToken;
   }

   public void setAccessToken(String accessToken) {
      this.accessToken = accessToken;
   }

   public String getLogPath() {
      return this.logPath;
   }

   public void setLogPath(String logPath) {
      this.logPath = logPath;
   }

   public Integer getLogRetentionDays() {
      return this.logRetentionDays;
   }

   public void setLogRetentionDays(Integer logRetentionDays) {
      this.logRetentionDays = logRetentionDays;
   }
}
