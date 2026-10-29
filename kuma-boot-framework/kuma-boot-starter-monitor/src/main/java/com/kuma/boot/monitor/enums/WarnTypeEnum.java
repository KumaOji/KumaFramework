package com.kuma.boot.monitor.enums;

public enum WarnTypeEnum {
   ERROR(2, "错误"),
   WARN(1, "告警"),
   INFO(0, "通知");

   private int level;
   private String description;

   public String getDescription() {
      return this.description;
   }

   public int getLevel() {
      return this.level;
   }

   private WarnTypeEnum(int level, String description) {
      this.description = description;
      this.level = level;
   }

   // $FF: synthetic method
   private static WarnTypeEnum[] $values() {
      return new WarnTypeEnum[]{ERROR, WARN, INFO};
   }
}
