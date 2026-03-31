package com.kuma.boot.monitor.alarm.core.entity;

import com.kuma.boot.monitor.alarm.core.execut.api.IExecute;
import java.util.List;

public class AlarmThreshold implements Comparable<AlarmThreshold> {
   private IExecute executor;
   private int min;
   private int max;
   private List<String> users;

   public AlarmThreshold() {
   }

   public int compareTo(AlarmThreshold o) {
      return o == null ? -1 : this.min - o.getMin();
   }

   public IExecute getExecutor() {
      return this.executor;
   }

   public void setExecutor(IExecute executor) {
      this.executor = executor;
   }

   public int getMin() {
      return this.min;
   }

   public void setMin(int min) {
      this.min = min;
   }

   public int getMax() {
      return this.max;
   }

   public void setMax(int max) {
      this.max = max;
   }

   public List<String> getUsers() {
      return this.users;
   }

   public void setUsers(List<String> users) {
      this.users = users;
   }
}
