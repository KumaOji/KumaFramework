package com.kuma.boot.monitor.enums;

public enum WarnTypeEnum {
   ERROR(2, "\u9519\u8bef"),
   WARN(1, "\u544a\u8b66"),
   INFO(0, "\u901a\u77e5");

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
