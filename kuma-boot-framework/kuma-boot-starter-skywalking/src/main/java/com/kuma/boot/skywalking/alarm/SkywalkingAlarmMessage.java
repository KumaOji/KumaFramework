package com.kuma.boot.skywalking.alarm;

public class SkywalkingAlarmMessage {
   private int scopeId;
   private String scope;
   private String name;
   private String id0;
   private String id1;
   private String ruleName;
   private String alarmMessage;
   private long startTime;
   private AlarmTags tags;
   private transient int period;
   private transient boolean onlyAsCondition;

   public SkywalkingAlarmMessage() {
   }

   public AlarmTags getTags() {
      return this.tags;
   }

   public void setTags(AlarmTags tags) {
      this.tags = tags;
   }

   public int getScopeId() {
      return this.scopeId;
   }

   public void setScopeId(int scopeId) {
      this.scopeId = scopeId;
   }

   public String getScope() {
      return this.scope;
   }

   public void setScope(String scope) {
      this.scope = scope;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getId0() {
      return this.id0;
   }

   public void setId0(String id0) {
      this.id0 = id0;
   }

   public String getId1() {
      return this.id1;
   }

   public void setId1(String id1) {
      this.id1 = id1;
   }

   public String getRuleName() {
      return this.ruleName;
   }

   public void setRuleName(String ruleName) {
      this.ruleName = ruleName;
   }

   public String getAlarmMessage() {
      return this.alarmMessage;
   }

   public void setAlarmMessage(String alarmMessage) {
      this.alarmMessage = alarmMessage;
   }

   public long getStartTime() {
      return this.startTime;
   }

   public void setStartTime(long startTime) {
      this.startTime = startTime;
   }

   public int getPeriod() {
      return this.period;
   }

   public void setPeriod(int period) {
      this.period = period;
   }

   public boolean isOnlyAsCondition() {
      return this.onlyAsCondition;
   }

   public void setOnlyAsCondition(boolean onlyAsCondition) {
      this.onlyAsCondition = onlyAsCondition;
   }

   public static class AlarmTags {
      private String key;
      private String value;

      public AlarmTags() {
      }

      public String getKey() {
         return this.key;
      }

      public void setKey(String key) {
         this.key = key;
      }

      public String getValue() {
         return this.value;
      }

      public void setValue(String value) {
         this.value = value;
      }
   }
}
