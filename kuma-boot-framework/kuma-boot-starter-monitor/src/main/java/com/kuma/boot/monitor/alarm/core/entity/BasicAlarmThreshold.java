package com.kuma.boot.monitor.alarm.core.entity;

import java.util.List;

public class BasicAlarmThreshold {
   private String level;
   private int threshold;
   private Integer max;
   private List<String> users;

   public BasicAlarmThreshold() {
   }

   public String getLevel() {
      return this.level;
   }

   public void setLevel(String level) {
      this.level = level;
   }

   public int getThreshold() {
      return this.threshold;
   }

   public void setThreshold(int threshold) {
      this.threshold = threshold;
   }

   public Integer getMax() {
      return this.max;
   }

   public void setMax(Integer max) {
      this.max = max;
   }

   public List<String> getUsers() {
      return this.users;
   }

   public void setUsers(List<String> users) {
      this.users = users;
   }
}
