package com.kuma.boot.monitor.alarm.core.entity;

import com.kuma.boot.monitor.alarm.core.execut.api.IExecute;
import java.util.List;

public class AlarmConfig {
   public static final int DEFAULT_MIN_NUM = 0;
   public static final int DEFAULT_MAX_NUM = 30;
   private List<String> users;
   private List<AlarmThreshold> alarmThreshold;
   private int minLimit;
   private int maxLimit;
   private IExecute executor;
   private boolean autoIncEmergency;

   public AlarmConfig() {
   }

   public List<String> getUsers() {
      return this.users;
   }

   public void setUsers(List<String> users) {
      this.users = users;
   }

   public List<AlarmThreshold> getAlarmThreshold() {
      return this.alarmThreshold;
   }

   public void setAlarmThreshold(List<AlarmThreshold> alarmThreshold) {
      this.alarmThreshold = alarmThreshold;
   }

   public int getMinLimit() {
      return this.minLimit;
   }

   public void setMinLimit(int minLimit) {
      this.minLimit = minLimit;
   }

   public int getMaxLimit() {
      return this.maxLimit;
   }

   public void setMaxLimit(int maxLimit) {
      this.maxLimit = maxLimit;
   }

   public IExecute getExecutor() {
      return this.executor;
   }

   public void setExecutor(IExecute executor) {
      this.executor = executor;
   }

   public boolean isAutoIncEmergency() {
      return this.autoIncEmergency;
   }

   public void setAutoIncEmergency(boolean autoIncEmergency) {
      this.autoIncEmergency = autoIncEmergency;
   }
}
