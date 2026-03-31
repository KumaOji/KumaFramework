package com.kuma.boot.monitor.alarm.core.entity;

import com.kuma.boot.monitor.alarm.core.execut.spi.NoneExecute;
import java.util.Collections;
import java.util.List;

public class BasicAlarmConfig {
   private List<String> users;
   private List<BasicAlarmThreshold> threshold;
   private Integer min = 0;
   private Integer max = 30;
   private String level;
   private boolean autoIncEmergency;

   public BasicAlarmConfig() {
      this.level = NoneExecute.NAME;
      this.autoIncEmergency = false;
   }

   public List<BasicAlarmThreshold> getThreshold() {
      return this.threshold == null ? Collections.emptyList() : this.threshold;
   }

   public List<String> getUsers() {
      return this.users;
   }

   public void setUsers(List<String> users) {
      this.users = users;
   }

   public void setThreshold(List<BasicAlarmThreshold> threshold) {
      this.threshold = threshold;
   }

   public Integer getMin() {
      return this.min;
   }

   public void setMin(Integer min) {
      this.min = min;
   }

   public Integer getMax() {
      return this.max;
   }

   public void setMax(Integer max) {
      this.max = max;
   }

   public String getLevel() {
      return this.level;
   }

   public void setLevel(String level) {
      this.level = level;
   }

   public boolean isAutoIncEmergency() {
      return this.autoIncEmergency;
   }

   public void setAutoIncEmergency(boolean autoIncEmergency) {
      this.autoIncEmergency = autoIncEmergency;
   }
}
