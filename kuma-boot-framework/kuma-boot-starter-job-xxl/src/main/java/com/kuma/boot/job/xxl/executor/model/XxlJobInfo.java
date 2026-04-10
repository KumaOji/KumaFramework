package com.kuma.boot.job.xxl.executor.model;

import java.util.Date;

public class XxlJobInfo {
   private int id;
   private int jobGroup;
   private String jobDesc;
   private Date addTime;
   private Date updateTime;
   private String author;
   private String alarmEmail;
   private String scheduleType;
   private String scheduleConf;
   private String misfireStrategy;
   private String executorRouteStrategy;
   private String executorHandler;
   private String executorParam;
   private String executorBlockStrategy;
   private int executorTimeout;
   private int executorFailRetryCount;
   private String glueType;
   private String glueSource;
   private String glueRemark;
   private Date glueUpdatetime;
   private String childJobId;
   private int triggerStatus;
   private long triggerLastTime;
   private long triggerNextTime;

   public XxlJobInfo() {
   }

   public int getId() {
      return this.id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public int getJobGroup() {
      return this.jobGroup;
   }

   public void setJobGroup(int jobGroup) {
      this.jobGroup = jobGroup;
   }

   public String getJobDesc() {
      return this.jobDesc;
   }

   public void setJobDesc(String jobDesc) {
      this.jobDesc = jobDesc;
   }

   public Date getAddTime() {
      return this.addTime;
   }

   public void setAddTime(Date addTime) {
      this.addTime = addTime;
   }

   public Date getUpdateTime() {
      return this.updateTime;
   }

   public void setUpdateTime(Date updateTime) {
      this.updateTime = updateTime;
   }

   public String getAuthor() {
      return this.author;
   }

   public void setAuthor(String author) {
      this.author = author;
   }

   public String getAlarmEmail() {
      return this.alarmEmail;
   }

   public void setAlarmEmail(String alarmEmail) {
      this.alarmEmail = alarmEmail;
   }

   public String getScheduleType() {
      return this.scheduleType;
   }

   public void setScheduleType(String scheduleType) {
      this.scheduleType = scheduleType;
   }

   public String getScheduleConf() {
      return this.scheduleConf;
   }

   public void setScheduleConf(String scheduleConf) {
      this.scheduleConf = scheduleConf;
   }

   public String getMisfireStrategy() {
      return this.misfireStrategy;
   }

   public void setMisfireStrategy(String misfireStrategy) {
      this.misfireStrategy = misfireStrategy;
   }

   public String getExecutorRouteStrategy() {
      return this.executorRouteStrategy;
   }

   public void setExecutorRouteStrategy(String executorRouteStrategy) {
      this.executorRouteStrategy = executorRouteStrategy;
   }

   public String getExecutorHandler() {
      return this.executorHandler;
   }

   public void setExecutorHandler(String executorHandler) {
      this.executorHandler = executorHandler;
   }

   public String getExecutorParam() {
      return this.executorParam;
   }

   public void setExecutorParam(String executorParam) {
      this.executorParam = executorParam;
   }

   public String getExecutorBlockStrategy() {
      return this.executorBlockStrategy;
   }

   public void setExecutorBlockStrategy(String executorBlockStrategy) {
      this.executorBlockStrategy = executorBlockStrategy;
   }

   public int getExecutorTimeout() {
      return this.executorTimeout;
   }

   public void setExecutorTimeout(int executorTimeout) {
      this.executorTimeout = executorTimeout;
   }

   public int getExecutorFailRetryCount() {
      return this.executorFailRetryCount;
   }

   public void setExecutorFailRetryCount(int executorFailRetryCount) {
      this.executorFailRetryCount = executorFailRetryCount;
   }

   public String getGlueType() {
      return this.glueType;
   }

   public void setGlueType(String glueType) {
      this.glueType = glueType;
   }

   public String getGlueSource() {
      return this.glueSource;
   }

   public void setGlueSource(String glueSource) {
      this.glueSource = glueSource;
   }

   public String getGlueRemark() {
      return this.glueRemark;
   }

   public void setGlueRemark(String glueRemark) {
      this.glueRemark = glueRemark;
   }

   public Date getGlueUpdatetime() {
      return this.glueUpdatetime;
   }

   public void setGlueUpdatetime(Date glueUpdatetime) {
      this.glueUpdatetime = glueUpdatetime;
   }

   public String getChildJobId() {
      return this.childJobId;
   }

   public void setChildJobId(String childJobId) {
      this.childJobId = childJobId;
   }

   public int getTriggerStatus() {
      return this.triggerStatus;
   }

   public void setTriggerStatus(int triggerStatus) {
      this.triggerStatus = triggerStatus;
   }

   public long getTriggerLastTime() {
      return this.triggerLastTime;
   }

   public void setTriggerLastTime(long triggerLastTime) {
      this.triggerLastTime = triggerLastTime;
   }

   public long getTriggerNextTime() {
      return this.triggerNextTime;
   }

   public void setTriggerNextTime(long triggerNextTime) {
      this.triggerNextTime = triggerNextTime;
   }

   public String toString() {
      int var10000 = this.id;
      return "XxlJobInfo{id=" + var10000 + ", jobGroup=" + this.jobGroup + ", jobDesc='" + this.jobDesc + "', addTime=" + String.valueOf(this.addTime) + ", updateTime=" + String.valueOf(this.updateTime) + ", author='" + this.author + "', alarmEmail='" + this.alarmEmail + "', scheduleType='" + this.scheduleType + "', scheduleConf='" + this.scheduleConf + "', misfireStrategy='" + this.misfireStrategy + "', executorRouteStrategy='" + this.executorRouteStrategy + "', executorHandler='" + this.executorHandler + "', executorParam='" + this.executorParam + "', executorBlockStrategy='" + this.executorBlockStrategy + "', executorTimeout=" + this.executorTimeout + ", executorFailRetryCount=" + this.executorFailRetryCount + ", glueType='" + this.glueType + "', glueSource='" + this.glueSource + "', glueRemark='" + this.glueRemark + "', glueUpdatetime=" + String.valueOf(this.glueUpdatetime) + ", childJobId='" + this.childJobId + "', triggerStatus=" + this.triggerStatus + ", triggerLastTime=" + this.triggerLastTime + ", triggerNextTime=" + this.triggerNextTime + "}";
   }
}
